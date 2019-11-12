package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.dao.sql.TodoDaoSql
import com.dylanm.functionalTodoApp.db.sql.SqlEffectLift

trait DaoModule[I[_], DbEffect[_]] {
  def todoDao: I[TodoDao[DbEffect]]
}

class DaoModuleImpl[I[_]: Later: Monad, F[_]: Sync, DbEffect[_]: Monad](
  implicit DB: SqlEffectLift[F, DbEffect]
) extends DaoModule[I, DbEffect] {

  override lazy val todoDao: I[TodoDao[DbEffect]] = Later[I].later {
    new TodoDaoSql[F, DbEffect]
  }
}