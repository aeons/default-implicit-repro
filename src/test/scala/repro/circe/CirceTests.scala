package repro.circe

import io.circe._
import repro.{BodySerializer, serialize}

case class Inner(a: Int, b: Boolean, c: String)
object Inner {
  implicit val encoder: Encoder[Inner] =
    Encoder.forProduct3("a", "b", "c")(i => (i.a, i.b, i.c))
}

class CirceTestsWorks {
  // This works
  import repro.circe._
  implicitly[BodySerializer[Inner]]
}

class CirceTestsFails {
  // This fails with a missing implicit for Printer
  implicitly[BodySerializer[Inner]]
}
