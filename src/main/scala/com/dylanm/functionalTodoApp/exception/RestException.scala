package com.dylanm.functionalTodoApp.exception

import scala.util.control.NoStackTrace
import com.twitter.finagle.http.Status

/**
  * Handled by ExceptionFilter in predictable way, resulting in appropriate json error message
  */
sealed trait RestException extends RuntimeException with NoStackTrace {
  def errors: Seq[String]
  def status: Status
}

abstract class RestExceptionImpl private[exception] (val errors: Seq[String], val status: Status)
  extends RuntimeException(errors.mkString(", "))

final case class ResourceAlreadyExistsException(msg: String)
  extends RestExceptionImpl(Seq(msg), Status.BadRequest) with RestException

final case class ResourceNotFoundException(msg: String)
  extends RestExceptionImpl(Seq(msg), Status.NotFound) with RestException

final case class ValidationFailedException(errors1: Seq[String])
  extends RestExceptionImpl(errors1, Status.BadRequest) with RestException
