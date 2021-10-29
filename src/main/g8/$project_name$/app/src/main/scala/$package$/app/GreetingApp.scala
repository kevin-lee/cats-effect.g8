package $package$.app

import cats.effect.Sync
import cats.syntax.all._
import $package$.core.Greeter
import $package$.core.types.{Message, Name}

import scala.io.StdIn

trait GreetingApp[F[_]] {
  def run(message: Message): F[Unit]
}

object GreetingApp {
  def apply[F[_]: Sync]: GreetingApp[F] = new GreetingApp[F] {
    override def run(message: Message): F[Unit] = for {
      _          <- Sync[F].delay(println("Please enter your name: "))
      nameString <- Sync[F].delay(StdIn.readLine())
      _          <- Sync[F].delay(println("-----------------------"))
      name       <- Sync[F].pure(Name(nameString))
      greeter    <- Sync[F].pure(Greeter[F](message))
      _          <- greeter.greet(name)
    } yield ()
  }

}
