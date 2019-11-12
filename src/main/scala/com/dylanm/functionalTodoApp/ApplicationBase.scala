package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.Effect
import cats.effect.Sync
import com.dylanm.functionalTodoApp.module.CommonModule
import com.dylanm.functionalTodoApp.module.CommonModuleImpl
import com.dylanm.functionalTodoApp.module.ControllerModule
import com.dylanm.functionalTodoApp.module.ControllerModuleImpl
import com.dylanm.functionalTodoApp.module.DaoModule
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.ServerModule
import com.dylanm.functionalTodoApp.module.ServerModuleImpl
import com.dylanm.functionalTodoApp.module.ServiceModule
import com.dylanm.functionalTodoApp.module.ServiceModuleImpl
import com.dylanm.functionalTodoApp.module.WebModule
import com.dylanm.functionalTodoApp.module.WebModuleImpl
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig

/**
  * Assembled application that is abstracted away from database layer implementation
  *
  * @param config
  * @tparam I application classes initialization effect
  * @tparam F generic effect
  * @tparam DbEffect database effect
  */
abstract class ApplicationBase[I[_]: Later: Monad, F[_]: Effect, DbEffect[_]: Sync](config: ApplicationConfig) {

  lazy val commonModule: CommonModule[F, I] = new CommonModuleImpl[F, I](config.json)

  def dbModule: DbModule[F, DbEffect, I]

  def daoModule: DaoModule[DbEffect, I]

  lazy val serviceModule: ServiceModule[DbEffect, I] = new ServiceModuleImpl[DbEffect, I](daoModule)

  lazy val controllerModule: ControllerModule[F, I] = new ControllerModuleImpl[F, DbEffect, I](
    commonModule, serviceModule, dbModule)

  lazy val webModule: WebModule[F, I] = new WebModuleImpl[F, I](controllerModule, commonModule)

  lazy val serverModule: ServerModule[F, I] = new ServerModuleImpl[F, I](
    webModule, commonModule, config.server)
}