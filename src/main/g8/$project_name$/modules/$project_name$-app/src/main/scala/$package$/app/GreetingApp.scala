package $package$.app

import cats.effect.Sync
import cats.syntax.all._
import $package$.core.Greeter
import $package$.core.types.{Message, Name}

import scala.io.StdIn

trait GreetingApp[F[*]] {
  def run(message: Message, name: Name): F[Unit]
}

object GreetingApp {
  def apply[F[*]: Sync]: GreetingApp[F] = new GreetingApp[F] {
    override def run(message: Message, name: Name): F[Unit] = for {
      _          <- Sync[F].delay(println("Please enter your name: "))
      nameString <- Sync[F].delay(StdIn.readLine())
      _          <- Sync[F].delay(println("-----------------------"))
      name1      <- Sync[F].pure(Name(nameString))
      name2      <- Sync[F].pure(name)
      greeter    <- Sync[F].pure(Greeter[F](message))
      _          <- greeter.greet(name1)
      _          <- greeter.greet(name2)
    } yield ()
  }

}
