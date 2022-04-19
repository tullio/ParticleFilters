import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.funsuite.AnyFunSuite
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.indexing.NDArrayIndex

class nd4jTest extends AnyFunSuite:
  test("basic operation can be executed"){
    val a = Nd4j.create(Array(Array(1.0, 2.0), Array(3.0, 4.0)))
    val b = Nd4j.zeros(2,2)
    val c = Nd4j.ones(2,2)
    assert(a.shape.toSeq == Seq(2,2))
    assert(a.get(NDArrayIndex.point(0), NDArrayIndex.all)
      == Nd4j.create(Array(1.0, 2.0)))
    assert(a.get(NDArrayIndex.all, NDArrayIndex.point(0))
      == Nd4j.create(Array(1.0, 3.0)))
    assert(b.shape.toSeq == Seq(2,2))
    assert(c.shape.toSeq == Seq(2,2))
    val d = Nd4j.linspace(0.0, 10.0, 5, DataType.DOUBLE)
    assert(d == Nd4j.create(Array(0.0, 2.5, 5.0, 7.5, 10.0)))
    val e = a.add(1)
    assert(a == Nd4j.create(Array(Array(1.0, 2.0), Array(3.0, 4.0))))
    assert(e == Nd4j.create(Array(Array(2.0, 3.0), Array(4.0, 5.0))))
    val f = a.addi(1)
    assert(a == Nd4j.create(Array(Array(2.0, 3.0), Array(4.0, 5.0))))
    assert(f == Nd4j.create(Array(Array(2.0, 3.0), Array(4.0, 5.0))))
    val g = Nd4j.create(Array(Array(1.0), Array(2.0)))
    val h = a.mmul(g)
    assert(h == Nd4j.create(Array(Array(8.0), Array(14.0))))

    println(d)
  }

