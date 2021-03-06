package com.dylanm.functionalTodoApp.module

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger

import cats.Monad
import cats.effect.{Async, ContextShift}
import cats.implicits._
import com.dylanm.functionalTodoApp.db.TxManager
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectEval, SqlTxManager}
import com.dylanm.functionalTodoApp.module.config.DbConfig
import javax.sql.DataSource
import org.apache.commons.dbcp2.{DriverManagerConnectionFactory, PoolableConnectionFactory, PoolingDataSource}
import org.apache.commons.pool2.impl.GenericObjectPool
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

trait DbModule[I[_], F[_], DbEffect[_]] {
  def tx: I[TxManager[F, DbEffect]]
}

object DbModule {
  // scalastyle:off method.length
  def apply[I[_] : Later : Monad, F[_] : Async, DbEffect[_]](
                                                              config: DbConfig,
                                                              alwaysRollback: Boolean = false
                                                            )(implicit DE: SqlEffectEval[F, DbEffect]
                                                            ): DbModule[I, F, DbEffect] = new DbModule[I, F, DbEffect] {

    private val jdbcPool: I[ContextShift[F]] = Later[I].later {
      val jdbcPool = Executors.newFixedThreadPool(config.maxPoolSize, new ThreadFactory {
        private val id = new AtomicInteger()

        override def newThread(r: Runnable): Thread = {
          val t = new Thread(r)
          t.setDaemon(true)
          t.setName("jdbc-pool-" + id.incrementAndGet())
          t
        }
      })

      new ContextShift[F] {
        override def shift: F[Unit] =
          Async[F].async(cb => {
            jdbcPool.submit(() => {
              cb(Right(()))
            }.asInstanceOf[Runnable])
          })

        override def evalOn[A](ec: ExecutionContext)(fa: F[A]): F[A] = ???
      }
    }

    private val flyway: I[Unit] = Later[I].later {
      val flyway = Flyway.configure()
        .dataSource(config.url, config.user, config.password)
        .baselineOnMigrate(true)
        .load()

      // Start the migration
      flyway.migrate()
    }

    private val dataSource: I[DataSource] = Later[I].later {
      val connectionFactory = new DriverManagerConnectionFactory(config.url, config.user, config.password)
      // scalastyle:off null
      val poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null)
      // scalastyle:on null
      val connectionPool = new GenericObjectPool(poolableConnectionFactory)

      connectionPool.setMinIdle(config.minPoolSize)
      connectionPool.setMaxTotal(config.maxPoolSize)

      poolableConnectionFactory.setPool(connectionPool)

      new PoolingDataSource(connectionPool)
    }

    override val tx: I[TxManager[F, DbEffect]] = for {
      _ <- flyway
      pool <- jdbcPool
      dataSource <- dataSource
    } yield {
      new SqlTxManager[F, DbEffect](dataSource, pool, alwaysRollback)
    }
  }

  // scalastyle:on method.length
}
