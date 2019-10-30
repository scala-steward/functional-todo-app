package com.dylanm.functionalTodoApp.memory

import cats.Monad
import com.dylanm.functionalTodoApp.config.Later
import com.dylanm.functionalTodoApp.config.module.DaoModule
import com.dylanm.functionalTodoApp.dao.TodoDao

class MemoryDaoModule[F[_]: Monad, I[_]: Later] extends DaoModule[F, I] {
  override lazy val todoDao: I[TodoDao[F]] = Later[I].later {
    new MemoryTodoDao[F]
  }
}