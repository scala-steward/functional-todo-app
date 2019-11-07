package com.dylanm.functionalTodoApp.exception

import scala.util.control.NoStackTrace

abstract class RestException(msg: String, val status: Int) extends RuntimeException(msg) with NoStackTrace

case class ResourceAlreadyExistsException(msg: String) extends RestException(msg, 400)
case class ResourceNotFoundException(msg: String) extends RestException(msg, 404)
