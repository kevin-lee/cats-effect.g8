package $package$.app

import cats.effect.{ExitCode, IO, IOApp}
import $package$.core.types.Message

object MyApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    _ <- IO(println("======================="))
    _ <- GreetingApp[IO].run(Message("Hello"))
    _ <- IO(println("======================="))
  } yield ExitCode.Success
}
