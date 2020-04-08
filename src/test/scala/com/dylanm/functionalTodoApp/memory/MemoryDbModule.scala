package com.dylanm.functionalTodoApp.memory

import cats.arrow.FunctionK
import cats.~>
import com.dylanm.functionalTodoApp.module.Later
import com.dylanm.functionalTodoApp.module.DbModule
import com.dylanm.functionalTodoApp.db.TxManager

class MemoryDbModule[I[_]: Later, F[_]] extends DbModule[I, F, F] {

  override val tx: I[TxManager[F, F]] = Later[I].later {
    new TxManager[F, F] {
      override def tx: F ~> F = FunctionK.lift(impl)

      private def impl[A](t: F[A]): F[A] = t
    }
  }
}
