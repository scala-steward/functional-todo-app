package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import com.fasterxml.jackson.databind.SerializationFeature
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.dylanm.functionalTodoApp.module.config.JsonConfig
import com.dylanm.functionalTodoApp.logging.Log
import com.dylanm.functionalTodoApp.logging.LogImpl

trait CommonModule[F[_], I[_]] {
  def json: I[FinatraObjectMapper]

  def log: I[Log[F]]
}

class CommonModuleImpl[F[_]: Sync, I[_]: Later: Monad](
                                                        jsonConfig: JsonConfig
                                                      ) extends CommonModule[F, I] {

  override lazy val json: I[FinatraObjectMapper] = Later[I].later {
    val om = FinatraJacksonModule.provideScalaObjectMapper(null)
    if (jsonConfig.pretty) {
      om.configure(SerializationFeature.INDENT_OUTPUT, true)
    }
    FinatraJacksonModule.provideCamelCaseFinatraObjectMapper(om)
  }

  override lazy val log: I[Log[F]] = Later[I].later {
    new LogImpl[F]
  }
}
