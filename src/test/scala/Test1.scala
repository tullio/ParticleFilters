import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.funsuite.AnyFunSuite

class Test1 extends AnyFunSuite:
//  @Test def t1(): Unit =
 def t1(): Unit =
   assert("I was compiled by Scala 3. :)" == msg)
