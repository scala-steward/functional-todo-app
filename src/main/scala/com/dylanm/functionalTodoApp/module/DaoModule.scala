package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.dao.sql.TodoDaoSql
import com.dylanm.functionalTodoApp.db.sql.SqlEffectLift

trait DaoModule[I[_], DbEffect[_]] {
  def todoDao: I[TodoDao[DbEffect]]
}

object DaoModule {

  def apply[I[_] : Later : Monad, F[_] : Sync, DbEffect[_] : Monad](
                                                                     implicit DB: SqlEffectLift[F, DbEffect]
                                                                   ): DaoModule[I, DbEffect] = new DaoModule[I, DbEffect] {

    override val todoDao: I[TodoDao[DbEffect]] = Later[I].later {
      new TodoDaoSql[F, DbEffect]
    }
  }
}
