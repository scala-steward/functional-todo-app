package com.dylanm.functionalTodoApp.dao

import cats.effect.IO
import com.dylanm.functionalTodoApp.db.sql.SqlEffect
import com.dylanm.functionalTodoApp.int.IntegrationTest

// TodoDao test using embedded postgres
class DbTodoDaoTest extends TodoDaoTest[IO, SqlEffect[IO, ?]] with IntegrationTest {

}
