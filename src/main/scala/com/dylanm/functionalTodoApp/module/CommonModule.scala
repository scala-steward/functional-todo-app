package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import com.dylanm.functionalTodoApp.logging.{Log, LogImpl}
import com.dylanm.functionalTodoApp.service.JsonService

trait CommonModule[I[_], F[_]] {
  def json: I[JsonService[F]]

  def log: I[Log[F]]
}

object CommonModule {

  def apply[I[_] : Later : Monad, F[_] : Sync](
                                              ): CommonModule[I, F] = new CommonModule[I, F] {

    override val json: I[JsonService[F]] = Later[I].later {
      JsonService[F]
    }

    override val log: I[Log[F]] = Later[I].later {
      new LogImpl[F]
    }
  }
}
