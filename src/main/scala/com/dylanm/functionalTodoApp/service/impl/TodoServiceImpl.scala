package com.dylanm.functionalTodoApp.service.impl

import cats.MonadError
import cats.implicits._
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.exception.ResourceAlreadyExistsException
import com.dylanm.functionalTodoApp.exception.ResourceNotFoundException
import com.dylanm.functionalTodoApp.model.Todo
import com.dylanm.functionalTodoApp.service.TodoService

class TodoServiceImpl[F[_]](dao: TodoDao[F])(implicit ME: MonadError[F, Throwable]) extends TodoService[F] {

  override def list(): F[Seq[Todo]] = dao.list()

  override def get(id: String): F[Option[Todo]] = dao.get(id)

  override def create(todo: Todo): F[Todo] = for {
    _ <- dao.get(todo.id).ensure(ResourceAlreadyExistsException("Todo with this id already exists"))(_.isEmpty)
    r <- dao.save(todo)
  } yield r

  override def update(todo: Todo): F[Todo] = for {
    _ <- dao.get(todo.id).ensure(ResourceNotFoundException("Todo with this id not found"))(_.isDefined)
    r <- dao.save(todo)
  } yield r

  override def delete(id: String): F[Boolean] = dao.delete(id)
}
