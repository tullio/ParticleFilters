package com.sofken.pf.test
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.IsNull.nullValue
import com.sofken.pf.Particle
import org.junit.Test

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
}