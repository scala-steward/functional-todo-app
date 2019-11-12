package com.dylanm.functionalTodoApp.memory

import java.sql.Connection

import cats.Monad
import cats.effect.Effect
import com.dylanm.functionalTodoApp.Application
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.DaoModule
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectLift, SqlEffectEval}
import com.dylanm.functionalTodoApp.memory.MemoryApp._

class MemoryApp[I[_]: Later: Monad, F[_]: Effect]()
  extends Application[I, F, F](ApplicationConfig.testConfig) {

  override lazy val daoModule: DaoModule[F, I] = new MemoryDaoModule[F, I]

  override lazy val dbModule: DbModule[F, F, I] = new MemoryDbModule[F, I]
}

object MemoryApp {
  implicit def DB[F[_]]: SqlEffectLift[F, F] = new SqlEffectLift[F, F] {
    override def lift[A](f: Connection => F[A]): F[A] = ???
  }

  implicit def DE[F[_]]: SqlEffectEval[F, F] = new SqlEffectEval[F, F] {
    override def eval[A](f: F[A], c: Connection): F[A] = ???
  }

}