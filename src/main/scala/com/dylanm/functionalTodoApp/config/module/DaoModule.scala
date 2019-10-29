package com.dylanm.functionalTodoApp.config.module

import cats.Monad
import com.dylanm.functionalTodoApp.config.Later
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.dao.sql.TodoDaoSql
import com.dylanm.functionalTodoApp.db.Db

trait DaoModule[F[_], I[_]] {
  def todoDao: I[TodoDao[F]]
}

class DaoModuleImpl[F[_]: Db: Monad, I[_]: Later: Monad] extends DaoModule[F, I] {

  override lazy val todoDao: I[TodoDao[F]] = Later[I].later {
    new TodoDaoSql[F]
  }
}
