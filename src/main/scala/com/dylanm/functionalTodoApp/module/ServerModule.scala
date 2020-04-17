package com.dylanm.functionalTodoApp.module

import cats.Monad
import cats.effect.Effect
import cats.implicits._
import com.dylanm.functionalTodoApp.module.config.ServerConfig
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.Request
import com.twitter.util.{Await, Future}

trait ServerModule[I[_], F[_]] {
  def server: I[() => Unit]
}

object ServerModule {

  def apply[I[_] : Later : Monad, F[_] : Effect](
                                                  webModule: WebModule[I, F],
                                                  commonModule: CommonModule[I, F],
                                                  config: ServerConfig
                                                ): ServerModule[I, F] = new ServerModule[I, F] {

    override val server: I[() => Unit] = for {
      service <- webModule.service
      log <- commonModule.log
    } yield () => {
      val f0 = { (req: Request) =>
        val io = service(req)

        Future value Effect[F].toIO(io).unsafeRunSync
      }
      val addr = config.interface + ":" + config.port
      val server = Http.serve(addr, Service.mk(f0))
      log.logInfo(s"HTTP Server is listening on $addr")
      Await.ready(server)

    }
  }
}
