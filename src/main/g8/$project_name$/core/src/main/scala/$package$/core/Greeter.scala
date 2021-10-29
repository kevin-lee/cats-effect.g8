package $package$.core

import cats.effect.Sync
import types._

trait Greeter[F[_]] {
  def greet(name: Name): F[Unit]
}

object Greeter {
  def apply[F[_]: Sync](message: Message): Greeter[F] =
    (name: Name) => Sync[F].delay(println(s"\${message.message} \${name.name}"))
}
