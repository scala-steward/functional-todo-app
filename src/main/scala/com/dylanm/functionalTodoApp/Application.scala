package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.IO
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
import com.dylanm.functionalTodoApp.db.sql.SqlDb

class Application[I[_]: Later: Monad](config: ApplicationConfig) {

  lazy val commonModule: CommonModule[IO, I] = new CommonModuleImpl[IO, I](config.json)

  lazy val dbModule: DbModule[IO, SqlDb, I] = new DbModuleImpl[IO, I](config.db)

  lazy val daoModule: DaoModule[SqlDb, I] = new DaoModuleImpl[SqlDb, I]

  lazy val serviceModule: ServiceModule[SqlDb, I] = new ServiceModuleImpl[SqlDb, I](daoModule)

  lazy val controllerModule: ControllerModule[IO, I] = new ControllerModuleImpl[IO, SqlDb, I](serviceModule, dbModule)

  lazy val webModule: WebModule[IO, I] = new WebModuleImpl[IO, I](controllerModule, commonModule)

  lazy val serverModule: ServerModule[IO, I] = new ServerModuleImpl[I](webModule, config.server)
}