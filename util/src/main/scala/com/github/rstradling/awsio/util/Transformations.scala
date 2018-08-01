package com.github.rstradling.awsio.util

import cats.~>
import cats.effect.IO
import cats.implicits._
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletionException
import java.util.function.BiFunction
import monix.eval.Task
import monix.execution.Cancelable
import scala.util.Failure
import scala.util.Success

@SuppressWarnings(Array("org.wartremover.warts.Null"))
object Transformations {
  def toIO[A](cf: CompletableFuture[A]): IO[A] =
    IO.cancelable(cb => {
      val _ = cf.handle[Unit](new BiFunction[A, Throwable, Unit] {
        override def apply(result: A, err: Throwable): Unit = {
          err match {
            case null =>
              cb(Right(result))
            case _: CancellationException =>
              ()
            case ex: CompletionException if ex.getCause ne null =>
              cb(Left(ex.getCause))
            case ex =>
              cb(Left(ex))
          }
        }
      })
      IO(cf.cancel(true)).void
    })
  def fromCompletableFuture[A](cf: CompletableFuture[A]): Task[A] =
    Task.async((_, cb) => {
      val _ = cf.handle[Unit](new BiFunction[A, Throwable, Unit] {
        override def apply(result: A, err: Throwable): Unit = {
          err match {
            case null =>
              cb(Success(result))
            case _: CancellationException =>
              ()
            case ex: CompletionException if ex.getCause ne null =>
              cb(Failure(ex.getCause))
            case ex =>
              cb(Failure(ex))
          }
        }
      })
      Cancelable(() => {
        val _ = cf.cancel(true)
        ()
      })
    })

  implicit def completableFutureToIO[A]: CompletableFuture ~> IO =
    new (CompletableFuture ~> IO) {
      def apply[A](a: CompletableFuture[A]): IO[A] = toIO(a)
    }

}
