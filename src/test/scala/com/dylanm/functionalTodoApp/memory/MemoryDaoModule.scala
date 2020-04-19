package com.dylanm.functionalTodoApp.memory

import cats.Monad
import com.dylanm.functionalTodoApp.dao.TodoDao
import com.dylanm.functionalTodoApp.module.{DaoModule, Later}

class MemoryDaoModule[I[_] : Later, F[_] : Monad] extends DaoModule[I, F] {
  override val todoDao: I[TodoDao[F]] = Later[I].later {
    new MemoryTodoDao[F]
  }
}
