package com.dylanm.functionalTodoApp.model

import tethys.derivation.semiauto._

case class Todo(
  id: String,
  text: String
)

object Todo {
  implicit val writer = jsonWriter[Todo]
  implicit val reader = jsonReader[Todo]
}
