package com.dylanm.functionalTodoApp.acceptance

import cats.Eval
import cats.effect.IO
import com.twitter.finagle.http.RequestBuilder
import com.dylanm.functionalTodoApp.int.IntegrationApp
import com.dylanm.functionalTodoApp.psql.EmbeddedPostgres
import org.scalatest.FreeSpec

class TodoAcceptanceTest extends FreeSpec {
  import com.dylanm.functionalTodoApp.db.sql._
  val app = IntegrationApp.make[Eval, IO, SqlEffect[IO, ?]](EmbeddedPostgres.acceptanceInstance)
  val service = app.webModule.service.value
  val om = app.commonModule.json.value

  def d = System.nanoTime().toString

  "list" - {
    "does not fail" in {
      val r = get("/items")
      assert(r.statusCode == 200)
      assert(r.contentString.nonEmpty)
    }

    "contains newly created item" in {
      val id = d
      val text = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      val r = get("/items")
      assert(r.contentString.contains(id))
      assert(r.contentString.contains(text))
    }
  }

  "get" - {
    "returns existing item" in {
      val id = d
      val text = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      assert(get(s"/items/$id").contentString == s"""{"id":"$id","text":"$text"}""")
    }

    "fails on non-existing item" in {
      assert(get(s"/items/$d").statusCode == 404)
    }
  }

  "post" - {
    "creates new item" in {
      val id = d
      val text = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      assert(get(s"/items/$id").contentString == s"""{"id":"$id","text":"$text"}""")
    }

    "fails if item already exists" in {
      val id = d
      val text = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 400)
    }

    "validation is present" in {
      val r = post(s"/items/invalid-id", Map("text" -> Seq.fill(101)('A').mkString))
      assert(r.statusCode == 400)
      assert(r.contentString ==
        """{"errors":["id: must only contain alphanumeric characters or underscore","text: length is not less than or equal 100"]}""")
    }
  }

  "put" - {
    "updates existing item" in {
      val id = d
      val text = s"test-$d"
      val text2 = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      assert(put(s"/items/$id", Map("text" -> text2)).statusCode == 200)
      assert(get(s"/items/$id").contentString == s"""{"id":"$id","text":"$text2"}""")
    }

    "fails to update missing item" in {
      assert(put(s"/items/$d", Map("text" -> "text")).statusCode == 404)
    }
  }

  "delete" - {
    "deletes existing item" in {
      val id = d
      val text = s"test-$d"
      assert(post(s"/items/$id", Map("text" -> text)).statusCode == 200)
      assert(delete(s"/items/$id").statusCode == 200)
    }

    "does not fail on missing item" in {
      assert(delete(s"/items/$d").statusCode == 200)
    }
  }

  private def get(path: String) =
    service(RequestBuilder().url(s"http://local/api/v1$path").buildGet()).unsafeRunSync()

  private def post(path: String, content: Map[String, Any]) =
    service(RequestBuilder().url(s"http://local/api/v1$path").buildPost(om.writeValueAsBuf(content))).unsafeRunSync()

  private def put(path: String, content: Map[String, Any]) =
    service(RequestBuilder().url(s"http://local/api/v1$path").buildPut(om.writeValueAsBuf(content))).unsafeRunSync()

  private def delete(path: String) =
    service(RequestBuilder().url(s"http://local/api/v1$path").buildDelete()).unsafeRunSync()
}