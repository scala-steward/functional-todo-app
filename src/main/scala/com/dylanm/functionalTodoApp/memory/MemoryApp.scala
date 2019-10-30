package com.dylanm.functionalTodoApp.memory

import cats.Monad
import cats.arrow.FunctionK
import cats.effect.Effect
import cats.effect.IO
import cats.~>
import com.dylanm.functionalTodoApp.Application
import com.dylanm.functionalTodoApp.config.ApplicationConfig
import com.dylanm.functionalTodoApp.config.Later
import com.dylanm.functionalTodoApp.config.module.DaoModule
import com.dylanm.functionalTodoApp.config.module.DaoModuleImpl
import com.dylanm.functionalTodoApp.config.module.DbModule
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.db.TxManager
import com.dylanm.functionalTodoApp.db.sql.SqlDb

class MemoryApp[I[_]: Later: Monad, F[_]: Effect] extends Application[I, F](ApplicationConfig.testConfig) {
  import com.dylanm.functionalTodoApp.db.sql.db

  override lazy val daoModule: DaoModule[SqlDbF, I] = new DaoModuleImpl[SqlDbF, I] {
    override lazy val todoDao: I[TodoDao[SqlDbF]] = Later[I].later {
      new MemoryTodoDao[SqlDbF]
    }
  }

  override lazy val dbModule: DbModule[F, SqlDbF, I] = new DbModule[F, SqlDbF, I] {
    override lazy val tx: I[TxManager[F, SqlDbF]] = Later[I].later {
      new TxManager[F, SqlDbF] {
        override def tx: SqlDbF ~> F = FunctionK.lift(impl)

        private def impl[A](t: SqlDb[F, A]): F[A] = t.apply(null)
      }
    }
  }
}