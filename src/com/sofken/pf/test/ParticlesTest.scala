package com.sofken.pf.test
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.IsNull.nullValue
import com.sofken.pf.Particle
import org.junit.Test
import scala.Int
import com.sofken.pf.ParticlePlus._
import com.sofken.pf.Particles
import scala.collection.mutable.ArrayBuffer

class ParticleTest {

  @Test
  def defaultValueTest {
    val obj = Particles[Int]()
    println(obj)
    assertThat(obj, notNullValue())
    assertThat(obj.resize(3, 2, 0), is(3))
    assertThat(obj.number , is(3))
    val it = obj.iterator
    it.foreach(x => x.p.map(y => assertThat(y, is(0) )))
    assertThat(obj.resize(2, 2), is(2))
    assertThat(obj.number, is(2))
    obj.p.map(x => x.p.map(y =>  assertThat(y, is(0) )))
  }
  @Test
  def valuedInitializeTest {
    val obj = new Particles[Double]()
    assertThat(obj, notNullValue())
    assertThat(obj.resize(3, 2, 0), is(3))
    assertThat(obj.number , is(3))
    val it = obj.iterator
    it.foreach(x => 
      x.iterator.foreach(y => assertThat(y, is(0D) ))
      )
  }
  @Test
  def copyTest {
    val obj = new Particle[String](3)
    assertThat(obj, notNullValue())
  }
  @Test
  def overloadedPlusTest {
    val obj1 = new Particle[Int](3)
    val obj2 = new Particle[Int](3)
    obj1.set(ArrayBuffer[Int](1,3,5))
    obj2.set(ArrayBuffer[Int](2,4,6))
    println((obj1+obj2).p)
    assertThat((obj1+obj2).apply(0), is(3))
    assertThat((obj1+obj2).apply(1), is(7))
    assertThat((obj1+obj2).apply(2), is(11))
  }
    @Test
  def overloadedDoublePlusTest {
    val obj1 = new Particle[Double](3)
    val obj2 = new Particle[Double](3)
    obj1.p = ArrayBuffer[Double](1,3,5)
    obj2.p = ArrayBuffer[Double](2,4,6)
    println((obj1+obj2).p)
    assertThat((obj1+obj2).p(0), is(3.0))
    assertThat((obj1+obj2).p(1), is(7.0))
    assertThat((obj1+obj2).p(2), is(11.0))
  }
    @Test
    def applyUpdateTest {
      val obj = new Particles[Double]()
      val obj1 = new Particle[Double](3)
      val obj2 = new Particle[Double](3)
      for(i <- 0 to 2){
        obj1(i) = i * 2
        obj2(i) = i * 2 + 1
      }
      obj.resize(2, 3)
      obj(0) = obj1
      obj(1) = obj2
      for(i <- 0 to 2){
        assertThat(obj(0)(i), is(i*2.0))
        assertThat(obj(1)(i), is(i*2.0 + 1))
      }
      for(i <- 0 to 2){
        obj1(i) = i * 3
      }
      obj(0) = obj1
      for(i <- 0 to 2){
        assertThat(obj(0)(i), is(i*3.0))
        assertThat(obj(1)(i), is(i*2.0 + 1))
      }
      
    }

}