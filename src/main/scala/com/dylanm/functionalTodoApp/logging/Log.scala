package com.dylanm.functionalTodoApp.logging


trait Log[F[_]] {

  def logInfo(msg: => String): F[Unit]

  def logAudit[T](operation: String, params: String*)(f: F[T]): F[T]

  def logRequest(msg: => String): F[Unit]

  def logValidationError(msg: => String, ex: Throwable): F[Unit]

  def logUnexpectedError(msg: => String, ex: Throwable): F[Unit]
}
