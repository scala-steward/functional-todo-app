package com.dylanm.functionalTodoApp.int

import cats.Monad
import cats.effect.Effect
import cats.effect.Sync
import com.dylanm.functionalTodoApp.Application
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectEval, SqlEffectLift}
import com.dylanm.functionalTodoApp.module.{DbModule, Later}
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig
import com.dylanm.functionalTodoApp.module.config.DbConfig

object IntegrationApp {

  def apply[I[_] : Later : Monad, F[_] : Effect, DbEffect[_] : Sync](
    db: DbConfig,
    alwaysRollback: Boolean
  )(
    implicit
    DB: SqlEffectLift[F, DbEffect],
    DE: SqlEffectEval[F, DbEffect]
  ): Application[I, F, DbEffect] = {

    val cfg = ApplicationConfig.testConfig.copy(db = db)

    val app = new Application[I, F, DbEffect](cfg)

    Later[I].setMock(app.dbModule,
      DbModule[I, F, DbEffect](cfg.db, alwaysRollback = alwaysRollback)
    )

    app
  }
}
