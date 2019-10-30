package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.Async
import cats.effect.Effect
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


class ApplicationBackend[I[_]: Later: Monad, F[_]: Async](config: ApplicationConfig) {
  import com.dylanm.functionalTodoApp.db.sql.db

  type SqlDbF[A] = SqlDb[F, A]

  lazy val commonModule: CommonModule[F, I] = new CommonModuleImpl[F, I](config.json)

  lazy val dbModule: DbModule[F, SqlDbF, I] = new DbModuleImpl[F, I](config.db)

  lazy val daoModule: DaoModule[SqlDbF, I] = new DaoModuleImpl[SqlDbF, I]

  lazy val serviceModule: ServiceModule[SqlDbF, I] = new ServiceModuleImpl[SqlDbF, I](daoModule)

  lazy val controllerModule: ControllerModule[F, I] = new ControllerModuleImpl[F, SqlDbF, I](serviceModule, dbModule)
}

class Application[I[_]: Later: Monad, F[_]: Effect](config: ApplicationConfig) {
  import com.dylanm.functionalTodoApp.db.sql.db

  type SqlDbF[A] = SqlDb[F, A]

  lazy val commonModule: CommonModule[F, I] = new CommonModuleImpl[F, I](config.json)

  lazy val dbModule: DbModule[F, SqlDbF, I] = new DbModuleImpl[F, I](config.db)

  lazy val daoModule: DaoModule[SqlDbF, I] = new DaoModuleImpl[SqlDbF, I]

  lazy val serviceModule: ServiceModule[SqlDbF, I] = new ServiceModuleImpl[SqlDbF, I](daoModule)

  lazy val controllerModule: ControllerModule[F, I] = new ControllerModuleImpl[F, SqlDbF, I](serviceModule, dbModule)

  lazy val webModule: WebModule[F, I] = new WebModuleImpl[F, I](controllerModule, commonModule)

  lazy val serverModule: ServerModule[F, I] = new ServerModuleImpl[F, I](webModule, config.server)
}