package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.dao.sql.TodoDaoSql
import com.dylanm.functionalTodoApp.db.sql.SqlEffectLift

trait DaoModule[DbEffect[_], I[_]] {
  def todoDao: I[TodoDao[DbEffect]]
}

class DaoModuleImpl[DbEffect[_]: Monad, F[_]: Sync, I[_]: Later: Monad](implicit DB: SqlEffectLift[DbEffect, F])
  extends DaoModule[DbEffect, I] {

  override lazy val todoDao: I[TodoDao[DbEffect]] = Later[I].later {
    new TodoDaoSql[DbEffect, F]
  }
}