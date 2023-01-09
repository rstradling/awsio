package com.github.rstradling.awsio.util

import cats.~>
import cats.effect.IO
import java.util.concurrent.CompletableFuture

object Transformations {
  def toIO[A](cf: => CompletableFuture[A]): IO[A] = {
    IO.fromCompletableFuture(IO(cf))
  }
  implicit def completableFutureToIO[A] : CompletableFuture ~> IO = new (CompletableFuture ~> IO) {
    def apply[A](a: CompletableFuture[A]): IO[A] = toIO(a)
  }

}
