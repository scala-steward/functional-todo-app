package com.dylanm.functionalTodoApp.http

import cats.effect.Sync
import cats.implicits._
import com.fasterxml.jackson.core.JsonProcessingException
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.finatra.json.internal.caseclass.exceptions.CaseClassMappingException
import com.dylanm.functionalTodoApp.exception.RestException
import com.dylanm.functionalTodoApp.logging.Log

/**
  * Convert error to REST error response
  *
  */
class ExceptionFilter[F[_]: Sync](
  om: FinatraObjectMapper,
  log: Log[F]
) extends Filter[F] {

  override def apply(orig: Request => F[Response]): Request => F[Response] = req => {

    Sync[F].defer(orig(req)).recoverWith {
      case e: RestException => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- respond(e.status, e.errors)
      } yield r

      case e: CaseClassMappingException => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- respond(Status.BadRequest, e.errors.map(_.getMessage()))
      } yield r

      case e: JsonProcessingException => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- {
          val loc = Option(e.getLocation).map { loc =>
            s" [line: ${loc.getLineNr}, column: ${loc.getColumnNr}]"
          }
          e.clearLocation()
          respond(Status.BadRequest, Seq(e.getMessage + loc.getOrElse("")))
        }
      } yield r

      case e: Throwable => for {
        _ <- log.logValidationError(e.getMessage, e)
        r <- respond(Status.InternalServerError, Seq("Internal Server Error"))
      } yield r
    }
  }

  private def respond(status: Status, errors: Seq[String]): F[Response] = {
    val r = Response(status)
    r.setContentTypeJson()
    r.content(om.writeValueAsBuf(Map("errors" -> errors.sorted)))
    r.pure[F]
  }
}
