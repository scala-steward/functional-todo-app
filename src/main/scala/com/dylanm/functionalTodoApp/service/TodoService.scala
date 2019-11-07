package com.dylanm.functionalTodoApp.service

import com.dylanm.functionalTodoApp.model.Todo

/**
  * Service layer - business logic designed to be called from other services and controllers
  *
  * @tparam F
  */
trait TodoService[F[_]] {
  def list(): F[Seq[Todo]]

  def get(id: String): F[Option[Todo]]

  def create(todo: Todo): F[Todo]

  def update(todo: Todo): F[Todo]

  def delete(id: String): F[Boolean]
}
