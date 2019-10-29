package com.dylanm.functionalTodoApp.dao

import com.dylanm.functionalTodoApp.model.Todo

trait TodoDao[F[_]] {
  def list(): F[Seq[Todo]]

  def get(id: String): F[Option[Todo]]

  def save(todo: Todo): F[Todo]

  def delete(id: String): F[Boolean]
}
