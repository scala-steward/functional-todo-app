package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.Effect
import cats.effect.Sync
import com.dylanm.functionalTodoApp.config.ApplicationConfig
import com.dylanm.functionalTodoApp.config.Later
import com.dylanm.functionalTodoApp.config.module.CommonModule
import com.dylanm.functionalTodoApp.config.module.CommonModuleImpl
import com.dylanm.functionalTodoApp.config.module.ControllerModule
import com.dylanm.functionalTodoApp.config.module.ControllerModuleImpl
import com.dylanm.functionalTodoApp.config.module.DaoModule
import com.dylanm.functionalTodoApp.config.module.DaoModuleImpl
import com.dylanm.functionalTodoApp.config.module.DbModule
import com.dylanm.functionalTodoApp.config.module.DbModuleImpl
import com.dylanm.functionalTodoApp.config.module.ServerModule
import com.dylanm.functionalTodoApp.config.module.ServerModuleImpl
import com.dylanm.functionalTodoApp.config.module.ServiceModule
import com.dylanm.functionalTodoApp.config.module.ServiceModuleImpl
import com.dylanm.functionalTodoApp.config.module.WebModule
import com.dylanm.functionalTodoApp.config.module.WebModuleImpl
import com.dylanm.functionalTodoApp.db.Db
import com.dylanm.functionalTodoApp.db.DbEval

class Application[I[_]: Later: Monad, F[_]: Effect, DbEffect[_]: Sync](config: ApplicationConfig)(
  implicit DB: Db[DbEffect, F], DE: DbEval[DbEffect, F]
) {

  lazy val commonModule: CommonModule[F, I] = new CommonModuleImpl[F, I](config.json)

  lazy val dbModule: DbModule[F, DbEffect, I] = new DbModuleImpl[F, DbEffect, I](config.db)

  lazy val daoModule: DaoModule[DbEffect, I] = new DaoModuleImpl[DbEffect, F, I]

  lazy val serviceModule: ServiceModule[DbEffect, I] = new ServiceModuleImpl[DbEffect, I](daoModule)

  lazy val controllerModule: ControllerModule[F, I] = new ControllerModuleImpl[F, DbEffect, I](serviceModule, dbModule)

  lazy val webModule: WebModule[F, I] = new WebModuleImpl[F, I](controllerModule, commonModule)

  lazy val serverModule: ServerModule[F, I] = new ServerModuleImpl[F, I](webModule, config.server)
}