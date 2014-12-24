package com.sofken.pf

import scala.reflect.ClassTag

case class Particle[A]() {
  var p = Vector[A]()
  var dimension:Int = _
  var initValue:A = _  
  def this(dimension:Int) = {
    this()
    this.dimension = dimension
    p = p.padTo(dimension, initValue)   
  }
    def this(dimension:Int, init:A) = {
    this()
    initValue = init
    this.dimension = dimension
    p = p.padTo(dimension, initValue)
  }
  def resize(dimension:Int):Int = {
    return resize(dimension, initValue)
  }
   def resize(dimension:Int, init:A):Int = {
    initValue = init
    if(dimension > this.dimension) {
      p = p.padTo(dimension - this.dimension , initValue)
      
    } else {
      p = p.take(dimension)
      
    }
    this.dimension = dimension
    return this.dimension

  }
   def +[A](t:Particle[A])(implicit s:ParticleOperator[A]):Particle[A] = {
     val q = p.zip(t.p).map{ case (x:A, y:A) => s.+(x, y) }
     val obj = Particle[A]()
     obj.p = q
     return obj
   }
}