package repro

import io.circe._

type BodySerializer[B] = B => String