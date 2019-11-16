package com.dylanm.functionalTodoApp.http

import cats.kernel.Monoid
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Request

/**
  * HTTP Route is a function that tries to convert request to response.
  * If request does not match this route, it returns None
  *
  * @tparam F
  * @tparam Resp
  */
trait Route[F[_], Resp] extends (Request => Option[F[Resp]]) {

  override def apply(req: Request): Option[F[Resp]]
}

object Route {
  def apply[F[_], Resp](f: Request => Option[F[Resp]]): Route[F, Resp] = req => f(req)

  def mk[F[_], Resp](method: Method, path: String)(f: (Request, Map[String, String]) => F[Resp]): Route[F, Resp] = {
    Route { req =>
      matches(req, method, path).map { pathParams =>
        f(req, pathParams)
      }
    }
  }

  private def matches(req: Request, method: Method, path: String): Option[Map[String, String]] = {
    // This is an intentionally imperative implementation
    // this method is performance critical so speed is more important than purity
    // This is probably a method to start looking at if you just want to learn some scala syntax

    if (req.method != method) return None

    val parts = path.split("/")
    val reqParts = req.path.split("/")

    if (parts.length != reqParts.length) return None

    val pairs = parts.zip(reqParts)

    if (!pairs.forall {case (part, reqPart) => part.startsWith(":") || part == reqPart}) return None

    Some(pairs.filter(_._1.startsWith(":")).toMap)
  }

  implicit def monoid[F[_], Resp] = new Monoid[Route[F, Resp]] {
    override def empty: Route[F, Resp] = Route(_ => None)

    override def combine(x: Route[F, Resp], y: Route[F, Resp]): Route[F, Resp] = req => x(req).orElse(y(req))
  }
}