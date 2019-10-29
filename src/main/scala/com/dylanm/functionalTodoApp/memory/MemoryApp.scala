package com.dylanm.functionalTodoApp.memory

import cats.Monad
import cats.arrow.FunctionK
import cats.effect.IO
import cats.effect.Sync
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

class MemoryApp[I[_]: Later: Monad] extends Application[I](ApplicationConfig.testConfig) {

  override lazy val daoModule: DaoModule[SqlDb, I] = new DaoModuleImpl[SqlDb, I] {
    override lazy val todoDao: I[TodoDao[SqlDb]] = Later[I].later {
      new MemoryTodoDao[SqlDb]
    }
  }

  override lazy val dbModule: DbModule[IO, SqlDb, I] = new DbModule[IO, SqlDb, I] {
    override lazy val tx: I[TxManager[IO, SqlDb]] = Later[I].later {
      new TxManager[IO, SqlDb] {
        override def tx: SqlDb ~> IO = FunctionK.lift(impl)

        private def impl[A](t: SqlDb[A]): IO[A] = Sync[IO].fromEither(t.f(null))
      }
    }
  }
}
