import org.scalatest.*
import flatspec.*
import matchers.*
import org.scalactic.*
import Tolerance.*
import com.example.pf._
import org.scalatest.funsuite.AnyFunSuite
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.indexing.NDArrayIndex

class TensorTest extends AnyFunSuite:
  test("basic operation can be executed"){
    val x = Tensor(Array(1.0, 2.0))
    assert(x.toString() == "[1.0000, 2.0000]")
    assert(x(0) == 1.0)
    assert(x(1) == 2.0)
    assert(x.length == 2)
    assert(x.map(f => f+1).toString() == "[2.0000, 3.0000]")
    val y = Tensor(Nd4j.create(Array(2.0, 3.0)))
    assert(y.toString() == "[2.0000, 3.0000]")
    val z = x + y
    println(z.toString() == "[3.0000, 5.0000]")
    val z1 = x + 1
    assert(z1.toString() == "[2.0000, 3.0000]")
    val z2 = x - y
    assert(z2.toString() == "[-1.0000, -1.0000]")
    val z3 = x - 1
    assert(z3.toString() == "[0, 1.0000]")
    val z4 = x dot y
    assert(z4.toString() == "[[8.0000]]")
    val z5 = x * 2
    assert(z5.toString() == "[2.0000, 4.0000]")
    //val z6 = x * y
    //assert(z4.toString() == "[2.0000, 6.0000]")

    val z7 = x.norm
    assert(z7 === (2.23606 +- 0.00001))
    val z8 = z5 / 2
    assert(z8.toString() == "[1.0000, 2.0000]")
    val a = Tensor(Array(1.0, 2.0, 4.0, 3.0))
    val b = a.cummulativeValues()
    assert(b.toString() == "[1.0000, 3.0000, 7.0000, 10.0000]")
    assert(b.indexWhere(f => f > 5) == 2)
  }
  test("statistical operation can be executed"){
    val a = Tensor(Array(1.1, 2.2, 2.5, 3.0, 3.1))
    val (bin, b) = a.histogram(3, 1.0, 4.0)
    assert(bin.toString() == "[1.0000, 2.0000, 3.0000]")
    assert(b.toString() == "[1.0000, 3.0000, 1.0000]")
    assert(b == Tensor(Array(1.0000, 3.0000, 1.0000)))
    val c = a.cummulativeValues()
    assert(c.toString() == "[1.1000, 3.3000, 5.8000, 8.8000, 11.9000]")
    val d = c/c.norm

    assert(d.==(Tensor(Array(0.0676, 0.2028, 0.3564, 0.5408, 0.7313))) (1e-4))
    println(d)

  }
  test("basic transformation can be executed") {
    val c = Tensor.repeat(1.3, 3)
    println(s"c=${c} (target)")
    println(s"supervised=${Tensor(Array(1.3, 1.3, 1.3))}")
    assert(c == Tensor(Array(1.3, 1.3, 1.3)))
    val a = Tensor(Array(1.1, 2.2, 2.5, 3.0, 3.1))
    val b = Array(1.1, 2.2, 2.5, 3.0, 3.1)
    val it = a.iterator
    val it2 = b.iterator
    while it.hasNext do
      val v = it.next()
      val v2 = it2.next()
      assert(v == v2)
  }