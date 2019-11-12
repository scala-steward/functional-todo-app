package com.dylanm.functionalTodoApp.memory

import cats.Monad
import cats.effect.Effect
import com.dylanm.functionalTodoApp.ApplicationBase
import com.dylanm.functionalTodoApp.module.DaoModule
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig

class MemoryApp[I[_]: Later: Monad, F[_]: Effect]() extends ApplicationBase[I, F, F](ApplicationConfig.testConfig) {

  override lazy val daoModule: DaoModule[F, I] = new MemoryDaoModule[F, I]

  override lazy val dbModule: DbModule[F, F, I] = new MemoryDbModule[F, I]
}