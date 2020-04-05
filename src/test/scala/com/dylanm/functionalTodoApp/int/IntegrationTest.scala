//package com.dylanm.functionalTodoApp.int
//
//import cats.effect.IO
//import com.dylanm.functionalTodoApp.module.Lazy
//import com.dylanm.functionalTodoApp.psql.EmbeddedPostgres
//import org.scalatest.FreeSpec
//
//abstract class IntegrationTest extends FreeSpec {
//  lazy val app = IntegrationTest.app
//}
//
//object IntegrationTest {
//  import com.dylanm.functionalTodoApp.db.sql._
//  lazy val app = IntegrationApp.make[Lazy, IO, SqlEffect[IO, ?]](
//    db = EmbeddedPostgres.instance,
//    alwaysRollback = true
//  )
//}
