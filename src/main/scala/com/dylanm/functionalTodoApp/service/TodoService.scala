package com.dylanm.functionalTodoApp.service

import com.dylanm.functionalTodoApp.model.Todo

trait TodoService[F[_]] {
  def list(): F[Seq[Todo]]

  def create(todo: Todo): F[Todo]

  def update(todo: Todo): F[Todo]

  def delete(id: String): F[Boolean]
}
