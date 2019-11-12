package com.dylanm.functionalTodoApp.int

import cats.Monad
import cats.effect.Effect
import cats.effect.Sync
import com.dylanm.functionalTodoApp.Application
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectLift, SqlEffectEval}
import com.dylanm.functionalTodoApp.module.DbModuleImpl
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig
import com.dylanm.functionalTodoApp.module.config.DbConfig

class IntegrationApp[I[_] : Later : Monad, F[_] : Effect, DbEffect[_] : Sync](config: ApplicationConfig)
                                                                             (implicit DB: SqlEffectLift[DbEffect, F],
                                                                              DE: SqlEffectEval[DbEffect, F])
  extends Application[I, F, DbEffect](config) {

  override lazy val dbModule = new DbModuleImpl[F, DbEffect, I](config.db, alwaysRollback = true)
}

object IntegrationApp {

  def make[I[_] : Later : Monad, F[_] : Effect, DbEffect[_] : Sync](db: DbConfig)
                                                                   (implicit DB: SqlEffectLift[DbEffect, F], DE: SqlEffectEval[DbEffect, F]):
  IntegrationApp[I, F, DbEffect] = {

    val cfg = ApplicationConfig.testConfig.copy(db = db)

    new IntegrationApp[I, F, DbEffect](cfg)
  }
}