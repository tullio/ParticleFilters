package com.sofken.pf.test
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.IsNull.nullValue
import com.sofken.pf.Particle
import org.junit.Test
import scala.Int
import scala.Boolean
import com.sofken.pf.ParticlePlus._
import com.sofken.pf.Filter

class FilterTest {

  @Test
  def defaultValueTest {
    val obj = new Filter()
    assertThat(obj, notNullValue())
    assertThat(obj.mt , notNullValue())
    obj.dimension = 3
    assertThat(obj.dimension, is(3))
  }
  @Test
  def valuedInitializeTest {
    val obj = new Filter()
    var f = obj.createInitialParticles
    assertThat(f, is(false))
    obj.number = 3
    obj.dimension = 2
    f = obj.createInitialParticles
    assertThat(f, is(true))
    
  }
  @Test
  def createSystemNoiseTest {
    val obj = new Filter()
    obj.number = 3
    obj.dimension = 2
    val f = obj.createInitialParticles
    assertThat(f, is(true))
    obj.createSystemNoise(0.0, 0.0)
    
  }

}