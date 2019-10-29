package com.dylanm.functionalTodoApp.config

case class DbConfig(
  url: String,
  user: String,
  password: String,
  minPoolSize: Int,
  maxPoolSize: Int
)
