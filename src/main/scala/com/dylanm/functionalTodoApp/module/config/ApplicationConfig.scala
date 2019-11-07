package com.dylanm.functionalTodoApp.module.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

case class ApplicationConfig(
  json: JsonConfig,
  server: ServerConfig,
  db: DbConfig
)

object ApplicationConfig {

  def load(c: Config): ApplicationConfig = ApplicationConfig(
    json = JsonConfig(
      pretty = c.getBoolean("app.json.pretty")
    ),
    server = ServerConfig(
      interface = c.getString("app.server.interface"),
      port = c.getInt("app.server.port")
    ),
    db = DbConfig(
      url = c.getString("app.db.url"),
      user = c.getString("app.db.user"),
      password = c.getString("app.db.password"),
      minPoolSize = c.getInt("app.db.minPoolSize"),
      maxPoolSize = c.getInt("app.db.maxPoolSize")
    )
  )

  val testConfig: ApplicationConfig = load(ConfigFactory.load("local"))
}