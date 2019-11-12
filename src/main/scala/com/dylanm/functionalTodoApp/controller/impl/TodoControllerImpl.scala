package com.dylanm.functionalTodoApp.controller.impl

import cats.data.ValidatedNel
import cats.effect.Sync
import cats.implicits._
import com.dylanm.functionalTodoApp.controller.TodoController
import com.dylanm.functionalTodoApp.controller.TodoRequest
import com.dylanm.functionalTodoApp.db.TxManager
import com.dylanm.functionalTodoApp.exception.ResourceNotFoundException
import com.dylanm.functionalTodoApp.exception.ValidationFailedException
import com.dylanm.functionalTodoApp.logging.Log
import com.dylanm.functionalTodoApp.model.Todo
import com.dylanm.functionalTodoApp.service.TodoService


class TodoControllerImpl[F[_]: Sync, DbEffect[_]](
 service: TodoService[DbEffect],
 tx: TxManager[F, DbEffect],
 log: Log[F]
) extends TodoController[F] {

  override def list(): F[Seq[Todo]] = log.logAudit("list") {
    tx.tx(service.list())
  }

  override def get(id: String): F[Todo] = log.logAudit("get", id)(for {
    todoOpt <- tx.tx(service.get(id))
    r <- todoOpt.fold(Sync[F].raiseError[Todo](ResourceNotFoundException("Item not found")))(_.pure[F])
  } yield r)

  override def create(id: String, todo: ValidatedNel[String, TodoRequest]): F[Todo] = log.logAudit("create", id) {
    for {
      todo <- lift(todo)
      r <- tx.tx(service.create(parse(id, todo)))
    } yield  r

  }

  override def update(id: String, todo: ValidatedNel[String, TodoRequest]): F[Todo] = log.logAudit("update", id) {
    for {
      todo <- lift(todo)
      r <- tx.tx(service.update(parse(id, todo)))
    } yield  r
  }

  override def delete(id: String): F[Boolean] = log.logAudit("delete", id) {
    tx.tx(service.delete(id))
  }

  private def lift[A](validated: ValidatedNel[String, A]): F[A] =
    validated.fold[F[A]](
      errors => Sync[F].raiseError(ValidationFailedException(errors.toList)),
      Sync[F].pure
    )

  private def parse(id: String, todo: TodoRequest): Todo = Todo(id = id, text = todo.text)
}