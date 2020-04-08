package com.dylanm.functionalTodoApp.psql

import com.dylanm.functionalTodoApp.module.config.DbConfig
import ru.yandex.qatools.embed.postgresql

object EmbeddedPostgres {
  lazy val instance = make()
  lazy val acceptanceInstance = make()

  private def make(): DbConfig = {
    val postgres = new postgresql.EmbeddedPostgres()

    val url = {
      val url: String = postgres.start()
      Runtime.getRuntime.addShutdownHook(new Thread(() => postgres.stop()))
      url
    }

    val user = postgresql.EmbeddedPostgres.DEFAULT_USER
    val password = postgresql.EmbeddedPostgres.DEFAULT_PASSWORD

    DbConfig(
      url = url,
      user = user,
      password = password,
      minPoolSize = 10,
      maxPoolSize = 10
    )
  }
}
