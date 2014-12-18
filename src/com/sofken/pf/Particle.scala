package com.sofken.pf


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
  println("constructor:" + p)
    println("parameter=" + init.getClass)
    println("constructor:" + init)
    initValue = init
      println("constructor:" + initValue.getClass)
    this.dimension = dimension
    p = p.padTo(dimension, initValue)
    println("my=" + this)
  }
  def resize(dimension:Int):Int = {
    return resize(dimension, initValue)
  }
   def resize(dimension:Int, init:A):Int = {
    initValue = init
    println(dimension)
    println(this.dimension)
    if(dimension > this.dimension) {
      p = p.padTo(dimension - this.dimension , initValue)
      
    } else {
      p = p.take(dimension)
      
    }
    this.dimension = dimension
    return this.dimension

  }
   def plus(t:Particle[A]):Particle[A] = {

     println("zipped = " + p.zip(t.p))
     println("start")
     println(p.zip(t.p).map{ case (x, y) => println("x="+x);println("y="+y)})
     println("----")
     println(p.zip(t.p).map{ case (x, y) => println(x.asInstanceOf[A].getClass);println(y.getClass)})
     println("end")
     val q = p.zip(t.p).map{ case (x:A, y:A) => x }
     println("result = " + q)
     val obj = Particle[A]()
     obj.p = q
     return obj
   }
}