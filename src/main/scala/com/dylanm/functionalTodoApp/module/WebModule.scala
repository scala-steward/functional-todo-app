package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status
import com.dylanm.functionalTodoApp.controller.TodoRequest
import com.dylanm.functionalTodoApp.http.ExceptionFilter
import com.dylanm.functionalTodoApp.http.Route

trait WebModule[I[_], F[_]] {
  def service: I[Request => F[Response]]
}

object WebModule {

  def apply[I[_]: Monad: Later, F[_]: Sync](
    controllerModule: ControllerModule[I, F],
    commonModule: CommonModule[I, F]
  ): WebModule[I, F] = new WebModule[I, F] {

    private val route: I[Route[F, Response]] = for {
      todoController <- controllerModule.todoController
      json <- commonModule.json
    } yield {

      def toJson[T](v: F[T]): F[Response] = v.map {v =>
        val r = Response(Status.Ok)
        r.setContentTypeJson()
        r.content = json.writeValueAsBuf(v)
        r
      }

      def fromJson[T: Manifest](req: Request): T = {
        json.parse[T](req.content)
      }

      List(
        Route.mk[F, Response](Method.Get, "/api/v1/items")((req, ctx) => toJson(todoController.list())),

        Route.mk[F, Response](Method.Get, "/api/v1/items/:id")((req, ctx) =>
          toJson(todoController.get(ctx(":id")))),

        Route.mk[F, Response](Method.Post, "/api/v1/items/:id")((req, ctx) =>
          toJson(todoController.create(ctx(":id"), fromJson[TodoRequest](req).validate(ctx(":id"))))
        ),

        Route.mk[F, Response](Method.Put, "/api/v1/items/:id")((req, ctx) =>
          toJson(todoController.update(ctx(":id"), fromJson[TodoRequest](req).validate(ctx(":id"))))
        ),

        Route.mk[F, Response](Method.Delete, "/api/v1/items/:id")((req, ctx) =>
          toJson(todoController.delete(ctx(":id")))
        )
      ).combineAll
    }

    override val service: I[Request => F[Response]] = for {
      route <- route
      om <- commonModule.json
      log <- commonModule.log
    } yield {
      val filters = new ExceptionFilter[F](om, log)

      filters(req => route(req).getOrElse(Response.apply(Status.NotFound).pure[F]))
    }
  }

}
