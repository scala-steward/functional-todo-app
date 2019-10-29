package com.dylanm.functionalTodoApp

import cats.Eval
import com.dylanm.functionalTodoApp.config.ApplicationConfig
import com.dylanm.functionalTodoApp.config.DbConfig
import com.dylanm.functionalTodoApp.config.JsonConfig
import com.dylanm.functionalTodoApp.config.ServerConfig


object Main {
  def main(args: Array[String]): Unit = {

    val config: ApplicationConfig = ApplicationConfig(
      json = JsonConfig(
        pretty = false
      ),
      server = ServerConfig(
        interface = "localhost",
        port = 8080
      ),
      db = DbConfig(
        url = "jdbc:postgresql://localhost:5432/functional-todo-app",
        user = "postgres",
        password = "",
        minPoolSize = 5,
        maxPoolSize = 10
      )
    )

    val app = new Application[Eval](config)

    val server = app.serverModule.server.value

    server()

  }
}