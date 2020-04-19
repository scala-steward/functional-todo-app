package com.dylanm.functionalTodoApp.dao

import cats.effect.IO
import com.dylanm.functionalTodoApp.memory.MemoryApp
import com.dylanm.functionalTodoApp.module.Lazy

// TodoDao test using in-memory dao implementation
class MemoryTodoDaoTest extends TodoDaoTest[IO, IO] {
  protected lazy val app = MemoryApp[Lazy, IO]()
}
