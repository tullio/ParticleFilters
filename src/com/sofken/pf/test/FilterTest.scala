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
import scala.collection.mutable.ArrayBuffer

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
    val SYS_DIST = 5.0
    val OBS_DIST = 1.0
    val DIM = 2
    def simpleState(p: Particles[Double], v: Particles[Double]) = {
      p + v
    }
    def simpleRobserve(p:Particles[Double], q:Particle[Double]) = {
      var diff = 0.0
//      p.p.foldLeft()((x,y) => 
//        for(i <- 0 to x.))
      for(i <- 0 to p.number - 1) {
        val x = p(i)
        var d = 0.0
        for(j <- 0 to x.dimension - 1) {
          d += (x(j)-q(j))*(x(j)-q(j))
        }
        diff += Math.sqrt(d)
      }
      diff/p.number 
    }
    def simpleRobserveJacobian(p:Particles[Double], q:Particle[Double]) = {
      1.0
    }
    def simpleRobserveDensity(w:Double) = {
      1.0/Math.sqrt(2.0*Math.PI*OBS_DIST*OBS_DIST) *
             Math.exp(-w*w/(2.0*OBS_DIST*OBS_DIST))
    }
    val obj = new Filter()
    obj.number = 3
    obj.dimension = DIM
        println(obj.getParticles.dump )
    val f = obj.createInitialParticles(0.0)
    assertThat(f, is(true))
    obj.setStateFunc(simpleState)
    obj.createSystemNoise(0.0, 1.0)
    val vs = obj.getSystemNoise
    obj.getNextState
    val ps = obj.getParticles
    println(ps.dump)
    // no sampling
    for(i <- 0 to obj.getParticles.number - 1) {
      val v = vs(i)
      val p = ps(i)
      for(j <- 0 to p.dimension - 1){
        assertThat(p(j), is(v(j)))
      }
    }
    obj.setRobserveFunc(simpleRobserve)
    obj.setRobserveJacobianFunc(simpleRobserveJacobian)
    obj.setRobserveDensityFunc(simpleRobserveDensity)
    var vt = ArrayBuffer[Double](1.0, 2.0)
    val y = new Particles[Double](obj.number , obj.dimension, 0.0)
    println("="+y.p.length )
    for(i <- 0 to vt.length - 1) {
      y(i).set(vt)
    }
    println("observed = " + y.dump)
    obj.setObservedData(y)
    obj.computLikelihood
    println(obj.alpha)
    obj.resampling
    println("resampled="+obj.getParticles.dump)
  }
}
