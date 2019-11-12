package com.dylanm.functionalTodoApp.memory

import cats.Monad
import cats.effect.Effect
import com.dylanm.functionalTodoApp.ApplicationBase
import com.dylanm.functionalTodoApp.module.DaoModule
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig

class MemoryApp[I[_]: Later: Monad, F[_]: Effect]() extends ApplicationBase[I, F, F](ApplicationConfig.testConfig) {

  override lazy val daoModule: DaoModule[I, F] = new MemoryDaoModule[I, F]

  override lazy val dbModule: DbModule[I, F, F] = new MemoryDbModule[I, F]
}