package com.sofken.pf


case class Particles[A]() {
  var p = scala.collection.mutable.ArrayBuffer[Particle[A]]()
  var number:Int = _
  var initValue:A = _
  def this(x:scala.collection.mutable.ArrayBuffer[Particle[A]]) = {
    this()
    number = x.length
    p = x
  }
  def this(number:Int, dimension:Int) = {
    this()
    this.number = number
    resize(number, dimension)
  }
  def resize(number:Int, dimension:Int, init:A):Int = {
	  initValue = init
	  if(number > this.number) {
		  p = p.padTo(number - this.number , new Particle(dimension, init))
	  } else {
		  p = p.take(number)
	  }
	  this.number = number
	  return this.number
  }
  def resize(number:Int, dimension:Int):Int = {
    return resize(number, dimension, initValue)
  }
     def apply(k:Int):Particle[A] = {
     p(k)
   }
   def update(k:Int, x:Particle[A]) = {
     p(k)=x
   }
   def iterator() = {
     p.iterator
   }
      def set(x:Particles[A]) = {
     p = x.p   
   }
   def set(x:scala.collection.mutable.ArrayBuffer[Particle[A]]) = {
     p = x
   }
   def +(t:Particles[A])(implicit s:ParticleOperator[A]):Particles[A] = {
//      def +[A](t:Particles[A]):Particles[A] = {
     println("+++++")

     println(p.zip(t.p).map{
       case (x:Particle[A], y:Particle[A]) => x.get.zip(y.get).map{
         case(x:A, y:A)=>s.+(x,y)}
       }) 
     new Particles[A](p.zip(t.p).map{
       case (x:Particle[A], y:Particle[A]) => new Particle[A](x.get.zip(y.get).map{
         case(a:A, b:A)=>s.+(a,b)})
       })
   }
   def dump:String = {
     p.iterator.foldLeft("[")((x,y) => x + "," + y) + ",]"
   }
}
