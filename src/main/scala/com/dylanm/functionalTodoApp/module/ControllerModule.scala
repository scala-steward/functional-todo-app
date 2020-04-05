package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.dylanm.functionalTodoApp.controller.TodoController
import com.dylanm.functionalTodoApp.controller.impl.TodoControllerImpl

trait ControllerModule[I[_], F[_]] {
  def todoController: I[TodoController[F]]
}

class ControllerModuleImpl[I[_]: Monad, F[_]: Sync, DbEffect[_]](
  commonModule: CommonModule[I, F],
  serviceModule: ServiceModule[I, DbEffect],
  dbModule: DbModule[I, F, DbEffect]
) extends ControllerModule[I, F] {

  override lazy val todoController: I[TodoController[F]] = for {
    todoService <- serviceModule.todoService
    tx <- dbModule.tx
    log <- commonModule.log
  } yield new TodoControllerImpl[F, DbEffect](todoService, tx = tx, log = log)
}
