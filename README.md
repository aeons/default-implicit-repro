## Compiler version

3.0.0-RC1

## Minimized code

The issue was found while looking at upgrading the sttp integration for circe to Scala 3 here: https://github.com/softwaremill/sttp/issues/864

The code works as is for Scala 2.

There's a reproduction here: https://github.com/aeons/default-implicit-repro

It looks like something has changed with how default implicit arguments work if they are used from a package object. There's a lot of moving parts here.

In the repro, these are the parts:
```scala
package repro.circe
trait SttpCirceApi {
  implicit def circeBodySerializer[B](implicit
      encoder: Encoder[B],
      printer: Printer = Printer.noSpaces
  ): BodySerializer[B] =
    b => encoder(b).printWith(printer)
}
```
```scala
package repro
package object circe extends SttpCirceApi
```
And then we have a test
```scala
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
```

## Output

```
[error] -- Error: /home/user/code/default-implicit-repro/src/test/scala/repro/circe/CirceTests.scala:20:35
[error] 20 |  implicitly[BodySerializer[Inner]]
[error]    |                                   ^
[error]    |no implicit argument of type repro.BodySerializer[repro.circe.Inner] was found for parameter e of method implicitly in object Predef.
[error]    |I found:
[error]    |
[error]    |    repro.circe.circeBodySerializer[repro.circe.Inner](repro.circe.Inner.encoder,
[error]    |      /* missing */summon[io.circe.Printer]
[error]    |    )
[error]    |
[error]    |But no implicit values were found that match type io.circe.Printer.
[error] one error found
[error] one error found
[error] (Test / compileIncremental) Compilation failed
[error] Total time: 1 s, completed Feb 23, 2021, 10:53:49 AM
```

## Expectation
It compiles and works, as in Scala 2.
