package com.dylanm.functionalTodoApp.web

import cats.effect.IO
import com.twitter.finagle.http.RequestBuilder
import com.twitter.io.Buf
import com.dylanm.functionalTodoApp.memory.MemoryApp
import com.dylanm.functionalTodoApp.module.Lazy
import org.scalatest.freespec.AnyFreeSpec

class WebTest extends AnyFreeSpec {
  val app = MemoryApp[Lazy, IO]()
  val service = app.webModule.value.flatMap(_.service.value).right.get

  "404 is empty response" in {
    val r = service(url("/404").buildGet()).unsafeRunSync()
    assert(r.statusCode == 404)
    assert(r.contentString.length == 0)
  }

  "empty request results in error message" in {
    val r = service(url("/items/142").buildPut(Buf.Empty)).unsafeRunSync()
    assert(r.statusCode == 400)
    assert(
      r.contentString == """{"errors":["Illegal json at '[ROOT]': Expected object start but found: Empty"]}""")
  }

  "invalid json results in error message" in {
    val r = service(url("/items/142").buildPut(Buf.Utf8("{\"a\": q}")))
      .unsafeRunSync()
    assert(r.statusCode == 400)
    assert(
      // can't get this test to pass without using a long line...
      // scalastyle:off
      r.contentString == """{"errors":["Unrecognized token 'q': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: (StringReader); line: 1, column: 8] line: 1, column: 8"]}""")
      // r.contentString == """{"errors":["Unrecognized token 'q': was expecting ('true', 'false' or 'null')\n at [Source: (StringReader); line: 1, column: 8] line: 1, column: 8"]}""")
    //scalastyle:on
  }

  "empty json results in validation message" in {
    val r = service(url("/items/142").buildPut(Buf.Utf8("{}"))).unsafeRunSync()
    assert(r.statusCode == 400)
    // see https://github.com/tethys-json/tethys/issues/51
    assert(
      r.contentString == """{"errors":["Illegal json at '[ROOT]': Can not extract fields from json'text'"]}""")
  }

  "unknown fields in request are ignored" in {
    val r = service(url("/items/142").buildPost(
      Buf.Utf8("""{"oops": 42, "id":"t1", "text": "text"}"""))).unsafeRunSync()
    assert(r.statusCode == 200)
    assert(r.contentString == """{"id":"142","text":"text"}""")
  }

  "nulls are not accepted for required fields" in {
    val r = service(
      url("/items/241").buildPost(Buf.Utf8("""{"id":"t1", "text": null}""")))
      .unsafeRunSync()
    assert(r.statusCode == 400)
    assert(
      r.contentString == """{"errors":["Illegal json at '[ROOT].text': Expected string value but found: NullValueToken"]}""")
  }

  private def url(path: String) =
    RequestBuilder().url(s"http://local/api/v1$path")

}
