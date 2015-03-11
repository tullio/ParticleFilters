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
import com.sofken.pf.Particles

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
    var f = obj.createInitialParticles(0.0)
    assertThat(f, is(false))
    obj.number = 3
    obj.dimension = 2
    f = obj.createInitialParticles(0.0)
    assertThat(f, is(true))
    
  }
  @Test
  def createSystemNoiseTest {
    val obj = new Filter()
    obj.number = 3
    obj.dimension = 2
    val f = obj.createInitialParticles(0.0)
    assertThat(f, is(true))
    obj.createSystemNoise(0.0, 1.0)
    
  }
  @Test
  def setGetStateFuncTest {
    def simpleState(p: Particles[Double], v: Particles[Double]) = {
      p + v
    }
    val obj = new Filter()
    obj.number = 3
    obj.dimension = 2
        println(obj.getParticles.dump )
    val f = obj.createInitialParticles(0.0)
    assertThat(f, is(true))
    obj.setStateFunc(simpleState)
    obj.createSystemNoise(0.0, 1.0)
    val vs = obj.getSystemNoise
    obj.getNextState
    val ps = obj.getParticles
    println(ps.dump)
    for(i <- 0 to obj.getParticles.number - 1) {
      val v = vs(i)
      val p = ps(i)
      for(j <- 0 to p.dimension - 1){
        assertThat(p(j), is(v(j)))
      }
    }
  }
  @Test
  def setGetObservedNoiseFuncTest {
    def simpleState(p: Particles[Double], v: Particles[Double]) = {
      p + v
    }
    def simpleRobserve(p:Particles[Double], q:Particle[Double]) = {
      var diff = 0.0
//      p.p.foldLeft()((x,y) => 
//        for(i <- 0 to x.))
      
    }
    val obj = new Filter()
    obj.number = 3
    obj.dimension = 2
        println(obj.getParticles.dump )
    val f = obj.createInitialParticles(0.0)
    assertThat(f, is(true))
    obj.setStateFunc(simpleState)
    obj.createSystemNoise(0.0, 1.0)
    val vs = obj.getSystemNoise
    obj.getNextState
    val ps = obj.getParticles
    println(ps.dump)
    for(i <- 0 to obj.getParticles.number - 1) {
      val v = vs(i)
      val p = ps(i)
      for(j <- 0 to p.dimension - 1){
        assertThat(p(j), is(v(j)))
      }
    }
  }
}
