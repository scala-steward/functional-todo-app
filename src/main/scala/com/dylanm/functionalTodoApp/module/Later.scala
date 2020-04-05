package com.dylanm.functionalTodoApp.module

/**
  * Typeclass for monads supporting lazy evaluation and result memoization.
  * Both properties are required for initialization effect.
  * @tparam I
  */
trait Later[I[_]] {
  def later[A](f: => A): I[A]
}

object Later {
  def apply[I[_]: Later]: Later[I] = implicitly[Later[I]]

  implicit val evalLater: Later[Lazy] = new Later[Lazy] {
    override def later[A](f: => A): Lazy[A] = Lazy(f)
  }
}
