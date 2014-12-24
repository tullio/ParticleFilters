package com.sofken.pf.test
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.IsNull.nullValue
import com.sofken.pf.Particle
import org.junit.Test
import scala.Int

import com.sofken.pf.ParticlePlus.IntPlus

class ParticleTest {

  @Test
  def defaultValueTest {
    val obj = new Particle[Int]()
    assertThat(obj, notNullValue())
    assertThat(obj.resize(3), is(3))
    assertThat(obj.dimension, is(3))
    val it = obj.p.iterator
    it.foreach(x => assertThat(x, is(0) ))
    assertThat(obj.resize(2), is(2))
    assertThat(obj.dimension, is(2))
  }
  @Test
  def valuedInitializeTest {
    val obj = new Particle[Double](3)
    val it = obj.p.iterator
    it.foreach(x => assertThat(x, is(0D) ))
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
    obj1.p = Vector[Int](1,3,5)
    obj2.p = Vector[Int](2,4,6)
    println((obj1+obj2).p)
    assertThat((obj1+obj2).p(0), is(3))
    assertThat((obj1+obj2).p(1), is(7))
    assertThat((obj1+obj2).p(2), is(11))
  }
    @Test
  def overloadedDoublePlusTest {
    import com.sofken.pf.ParticlePlus._
    val obj1 = new Particle[Double](3)
    val obj2 = new Particle[Double](3)
    obj1.p = Vector[Double](1,3,5)
    obj2.p = Vector[Double](2,4,6)
    println((obj1+obj2).p)
    assertThat((obj1+obj2).p(0), is(3.0))
    assertThat((obj1+obj2).p(1), is(7.0))
    assertThat((obj1+obj2).p(2), is(11.0))
  }
}