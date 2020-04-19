package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.{Effect, Sync}
import cats.implicits._
import com.dylanm.functionalTodoApp.db.sql.{SqlEffectEval, SqlEffectLift}
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig
import com.dylanm.functionalTodoApp.module._

class Application[I[_]: Later: Monad, F[_]: Effect, DbEffect[_]: Sync](
    config: ApplicationConfig
)(
    implicit
    DB: SqlEffectLift[F, DbEffect],
    DE: SqlEffectEval[F, DbEffect]
) {

  val commonModule: I[CommonModule[I, F]] = Later[I].later(CommonModule[I, F]())

  val dbModule: I[DbModule[I, F, DbEffect]] =
    Later[I].later(DbModule[I, F, DbEffect](config.db))

  val daoModule: I[DaoModule[I, DbEffect]] =
    Later[I].later(DaoModule[I, F, DbEffect])

  val serviceModule: I[ServiceModule[I, DbEffect]] = for {
    daoModule <- daoModule
  } yield ServiceModule[I, DbEffect](daoModule)

  val controllerModule: I[ControllerModule[I, F]] = for {
    commonModule <- commonModule
    serviceModule <- serviceModule
    dbModule <- dbModule
  } yield
    ControllerModule[I, F, DbEffect](commonModule, serviceModule, dbModule)

  val webModule: I[WebModule[I, F]] = for {
    controllerModule <- controllerModule
    commonModule <- commonModule
  } yield WebModule[I, F](controllerModule, commonModule)

  val serverModule: I[ServerModule[I, F]] = for {
    webModule <- webModule
    commonModule <- commonModule
  } yield ServerModule[I, F](webModule, commonModule, config.server)
}
