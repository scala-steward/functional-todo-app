package com.dylanm.functionalTodoApp.config.module

import cats.Monad
import cats.effect.Async
import javax.sql.DataSource
import com.dylanm.functionalTodoApp.config.DbConfig
import com.dylanm.functionalTodoApp.config.Later
import com.dylanm.functionalTodoApp.db.TxManager
import com.dylanm.functionalTodoApp.db.sql.SqlDb
import com.dylanm.functionalTodoApp.db.sql.SqlTxManager
import org.apache.commons.dbcp2.DriverManagerConnectionFactory
import org.apache.commons.dbcp2.PoolableConnectionFactory
import org.apache.commons.dbcp2.PoolingDataSource
import org.apache.commons.pool2.impl.GenericObjectPool
import cats.implicits._
import org.flywaydb.core.Flyway

trait DbModule[F[_], T[_], I[_]] {
  def tx: I[TxManager[F, T]]
}

class DbModuleImpl[F[_]: Async, I[_]: Later: Monad](config: DbConfig) extends DbModule[F, SqlDb, I] {

  override lazy val tx: I[TxManager[F, SqlDb]] = for {
    _ <- flyway
    dataSource <- dataSource
  } yield  {
    new SqlTxManager[F](dataSource, config.maxPoolSize)
  }

  private lazy val flyway: I[Unit] = Later[I].later {
    val flyway = Flyway.configure()
      .dataSource(config.url, config.user, config.password)
      .baselineOnMigrate(true)
      .load()

    // Start the migration
    flyway.migrate()
  }

  private lazy val dataSource: I[DataSource] = Later[I].later {
    val connectionFactory = new DriverManagerConnectionFactory(config.url, config.user, config.password)

    val poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null)

    val connectionPool = new GenericObjectPool(poolableConnectionFactory)

    connectionPool.setMinIdle(config.minPoolSize)
    connectionPool.setMaxTotal(config.maxPoolSize)

    poolableConnectionFactory.setPool(connectionPool)

    new PoolingDataSource(connectionPool)
  }
}