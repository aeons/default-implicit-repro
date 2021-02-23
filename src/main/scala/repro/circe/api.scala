package repro.circe

import io.circe._
import repro.BodySerializer

trait SttpCirceApi {
  implicit def circeBodySerializer[B](implicit
      encoder: Encoder[B],
      printer: Printer = Printer.noSpaces
  ): BodySerializer[B] =
    b => encoder(b).printWith(printer)
}
