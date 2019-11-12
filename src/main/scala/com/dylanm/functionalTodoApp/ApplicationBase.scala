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

  lazy val commonModule: CommonModule[I, F] = new CommonModuleImpl[I, F](config.json)

  def dbModule: DbModule[I, F, DbEffect]

  def daoModule: DaoModule[I, DbEffect]

  lazy val serviceModule: ServiceModule[I, DbEffect] = new ServiceModuleImpl[I, DbEffect](daoModule)

  lazy val controllerModule: ControllerModule[I, F] = new ControllerModuleImpl[I, F, DbEffect](
    commonModule, serviceModule, dbModule)

  lazy val webModule: WebModule[I, F] = new WebModuleImpl[I, F](controllerModule, commonModule)

  lazy val serverModule: ServerModule[I, F] = new ServerModuleImpl[I, F](
    webModule, commonModule, config.server)
}