package com.dylanm.functionalTodoApp.memory

import java.sql.Connection

import cats.Monad
import cats.effect.Effect
import com.dylanm.functionalTodoApp.Application
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectEval, SqlEffectLift}
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig

object MemoryApp {
  /**
    * Construct application using in-memory DAO instead of database
    */
  def apply[I[_]: Later: Monad, F[_]: Effect](): Application[I, F, F] = {

    // mock implementation of SQL effect (unused)
    implicit val sqlEff = new SqlEffectLift[F, F] with SqlEffectEval[F, F] {

      override def lift[A](f: Connection => F[A]): F[A] = f(null)

      override def eval[A](f: F[A], c: Connection): F[A] = f
    }

    val app = new Application[I, F, F](ApplicationConfig.testConfig)

    Later[I].setMock(app.daoModule, new MemoryDaoModule[I, F])
    Later[I].setMock(app.dbModule, new MemoryDbModule[I, F])

    app
  }
}
