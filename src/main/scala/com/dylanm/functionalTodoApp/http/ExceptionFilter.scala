package com.dylanm.functionalTodoApp.http

import cats.effect.Sync
import cats.implicits._
import com.dylanm.functionalTodoApp.exception.RestException
import com.dylanm.functionalTodoApp.logging.Log
import com.dylanm.functionalTodoApp.service.JsonService
import com.twitter.finagle.http.{Request, Response, Status}

/**
  * Convert error to REST error response
  *
  */
class ExceptionFilter[F[_] : Sync](
                                    om: JsonService[F],
                                    log: Log[F]
                                  ) extends Filter[F] {

  override def apply(orig: Request => F[Response]): Request => F[Response] = req => {

    Sync[F].defer(orig(req)).recoverWith {
      case e: RestException => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- respond(Status.fromCode(e.status), e.errors)
      } yield r

      case e: Throwable => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- respond(Status.InternalServerError, Seq("Internal Server Error"))
      } yield r
    }
  }

  private def respond(status: Status, errors: Seq[String]): F[Response] = {
    for {
      json <- om.write(ErrorResponse(errors.sorted))
    } yield {
      val resp = Response(status)
      resp.setContentTypeJson
      resp.contentString = json
      resp
    }
  }
}
