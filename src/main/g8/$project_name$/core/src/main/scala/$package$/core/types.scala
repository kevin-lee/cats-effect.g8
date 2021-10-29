package $package$.core

import io.estatico.newtype.macros.newtype

object types{

  @newtype case class Name(name: String)

  @newtype case class Message(message: String)

}
