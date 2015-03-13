package com.sofken.pf

import scala.collection.mutable.ArrayBuffer


case class Particle[A]() {
  var p = scala.collection.mutable.ArrayBuffer[A]()
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
  def this(x:scala.collection.mutable.ArrayBuffer[A]) = {
    this()
    this.dimension = x.length
    p = x
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
     new Particle[A](p.zip(t.p).map{ case (x:A, y:A) => s.+(x, y) })
   }
/*   trait plus[A] {
     def +(t:Particle[A]):Particle[A]
   }
   implicit object intPlus extends plus[Int] {
       //p(i) + t(i)
//       println(p.zip(t.p))
     def +(t:Particle[Int]):Particle[Int] = {
       new Particle[Int](p.zip(t.p).map { case (x:Int, y:Int) => (x+y) })
     }
   }
   def +[A](t:Particle[A])(implicit s: Particle[A]):Particle[A] = s.+(t)*/
   
//   def +(t:Particle[Int]):Particle[Int] = {
//       new Particle[Int](p.zip(t.p).map { case (x:Int, y:Int) => (x+y) })
//   }
//   def +(t:Particle[Double]):Particle[Double] = {
//       new Particle[Double](p.zip(t.p).map { case (x:Double, y:Double) => (x+y) })
//   }
   def apply(k:Int):A = {
     p(k)
   }
   def update(k:Int, x:A) = {
     p(k)=x
   }
   def iterator() = {
     p.iterator
   }
   def set(x:Particle[A]) = {
     p = x.p   
   }
   def set(x:scala.collection.mutable.ArrayBuffer[A]) = {
     p = x
   }
   def get() = p
   def dump() = {
     p.iterator.foldLeft("(")((x,y) => x + "," + y)+ ",)"
   }
   override def toString() = dump
}
