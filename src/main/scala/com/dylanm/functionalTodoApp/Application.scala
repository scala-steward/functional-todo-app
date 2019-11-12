
package com.dylanm.functionalTodoApp

import cats.Monad
import cats.effect.Effect
import cats.effect.Sync
import com.dylanm.functionalTodoApp.db.sql.SqlEffectEval
import com.dylanm.functionalTodoApp.db.sql.SqlEffectLift
import com.dylanm.functionalTodoApp.module.DaoModule
import com.dylanm.functionalTodoApp.module.DaoModuleImpl
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.module.DbModuleImpl
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.config.ApplicationConfig


class Application[I[_]: Later: Monad, F[_]: Effect, DbEffect[_]: Sync](config: ApplicationConfig)(
  implicit DB: SqlEffectLift[DbEffect, F], DE: SqlEffectEval[DbEffect, F]
) extends ApplicationBase[I, F, DbEffect](config) {

  override lazy val dbModule: DbModule[F, DbEffect, I] = new DbModuleImpl[F, DbEffect, I](config.db)

  override lazy val daoModule: DaoModule[DbEffect, I] = new DaoModuleImpl[DbEffect, F, I]()
}