package com.dylanm.functionalTodoApp.http

import tethys.derivation.semiauto._

case class ErrorResponse(errors: Seq[String])

object ErrorResponse {
  implicit val writer = jsonWriter[ErrorResponse]
}
