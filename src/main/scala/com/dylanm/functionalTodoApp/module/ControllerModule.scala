package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.dylanm.functionalTodoApp.controller.TodoController
import com.dylanm.functionalTodoApp.controller.impl.TodoControllerImpl

trait ControllerModule[I[_], F[_]] {
  def todoController: I[TodoController[F]]
}

object ControllerModule {

  def apply[I[_] : Monad, F[_] : Sync, DbEffect[_]](
                                                     commonModule: CommonModule[I, F],
                                                     serviceModule: ServiceModule[I, DbEffect],
                                                     dbModule: DbModule[I, F, DbEffect]
                                                   ): ControllerModule[I, F] = new ControllerModule[I, F] {

    override val todoController: I[TodoController[F]] = for {
      todoService <- serviceModule.todoService
      tx <- dbModule.tx
      log <- commonModule.log
    } yield new TodoControllerImpl[F, DbEffect](todoService, tx = tx, log = log)
  }
}
