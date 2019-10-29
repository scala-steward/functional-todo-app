package com.dylanm.functionalTodoApp.config

import cats.Eval

trait Later[F[_]] {
  def later[A](f: => A): F[A]
}

object Later {
  def apply[F[_]: Later]: Later[F] = implicitly[Later[F]]

  implicit val evalLater: Later[Eval] = new Later[Eval] {
    override def later[A](f: => A): Eval[A] = cats.Later(f)
  }
}
