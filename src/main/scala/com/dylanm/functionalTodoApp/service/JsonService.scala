package com.dylanm.functionalTodoApp.service

import cats.effect.Sync
import cats.implicits._
import com.dylanm.functionalTodoApp.exception.ValidationFailedException
import com.fasterxml.jackson.core.JsonParseException
import tethys._
import tethys.jackson._


trait JsonService[F[_]] {
  def read[T: JsonReader](json: String): F[T]

  def write[T: JsonWriter](value: T): F[String]
}

object JsonService {
  def apply[F[_]: Sync]: JsonService[F] = new JsonService[F] {
    override def read[T: JsonReader](json: String): F[T] =
      json.jsonAs[T] match {
        case Left(error) if error.getCause.isInstanceOf[JsonParseException] =>
          val err = error.getCause.asInstanceOf[JsonParseException]
          val location = Option(err.getLocation).map { location =>
            s" line: ${location.getLineNr}, column: ${location.getColumnNr}"
          }
          val msg = err.getMessage + location.getOrElse("")
          Sync[F].raiseError(ValidationFailedException(Seq(msg)))

        case Left(error) => Sync[F].raiseError(ValidationFailedException(Seq(error.getMessage)))

        case Right(value)  => value.pure[F]
      }

    override def write[T: JsonWriter](value: T): F[String] =
      value.asJson.pure[F]
  }
}
