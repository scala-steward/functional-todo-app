package com.dylanm.functionalTodoApp.controller.impl

import com.dylanm.functionalTodoApp.controller.TodoController
import com.dylanm.functionalTodoApp.db.TxManager
import com.dylanm.functionalTodoApp.model.Todo
import com.dylanm.functionalTodoApp.service.TodoService

class TodoControllerImpl[F[_], T[_]](
                                      service: TodoService[T],
                                      tx: TxManager[F, T]
                                    ) extends TodoController[F] {

  override def list(): F[Seq[Todo]] = {
    tx.tx(service.list())
  }

  override def create(todo: Todo): F[Todo] = {
    tx.tx(service.create(todo))
  }

  override def update(todo: Todo): F[Todo] = {
    tx.tx(service.update(todo))
  }

  override def delete(id: String): F[Boolean] = {
    tx.tx(service.delete(id))
  }
}
