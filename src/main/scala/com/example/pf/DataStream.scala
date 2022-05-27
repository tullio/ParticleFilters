package com.example.pf

import breeze.plot.{Figure, Plot, plot}
import com.example.pf.model.{LinearGaussianObservationModel, LinearGaussianSystemModel}

class DataStream() extends gui:
    var dataStream: Tensor = _
    var timeStream: Tensor = _
    var lastIndex = -1 // No data
    def push(t: Tensor, d: Tensor) =
      if lastIndex == -1 then
        val dimension = d.shape(0).toInt
        println(d.reshape(1, d.shape(0).toInt))
        dataStream = d.reshape(1, d.shape(0).toInt)
        println(s"dataStream=${dataStream},${dataStream.shape.toSeq}")
        timeStream = t.reshape(1, t.shape(0).toInt)
        lastIndex = 0
      else
        timeStream.push(t)
        dataStream.push(d)

      this

    def tuning =
        val systemNoiseParameterRange = 2
        val observationNoiseParameterRange = 2
        var f: Figure = null
        var p0: Plot = null
        if enableX11 then
            f = Figure(s"Particle filter")
            f.width = 1480
            f.height = 740

        for
            i <- Range(-systemNoiseParameterRange, systemNoiseParameterRange, 2)
            j <- Range(-observationNoiseParameterRange, observationNoiseParameterRange, 1)
        do
          val a = math.pow(2.0, i) // variance of noise of the system model
          val b = math.pow(2.0, j) // variance of noise of the observation model
          val systemModel = LinearGaussianSystemModel(0.0, a)
          val inversedObservationModel = LinearGaussianObservationModel(0.0, b)
          val filter = ParticleFilter(systemModel, inversedObservationModel)
          val timeIter = timeStream.tensorIterator
          val dataIter = dataStream.tensorIterator
          val startTime = timeIter.next()
          val startData = dataIter.next()
          var x = Tensor.repeat(startData, 10)
          var likelihood = 0.0
          while dataIter.hasNext do
              val y = dataIter.next()
              val t0 = timeIter.next()
              x = filter.step(x, y)
              val t = Tensor.repeat(t0, x.length.toInt)
              val predict = x.mean(0)
              if enableX11 then
                  p0 += plot(t.toArray, x.toArray, name = "predict particle", style = '.')
                  p0 += plot(Array(t0), Array(predict), name = "predict value", style = '+', colorcode = "[255,0,0]")

                  val l = filter.logLikelihood

                  likelihood += l
          println(s"logLikelyhood=${likelihood}(a=${a}, b=${b})")

  override def toString() =
        dataStream.toString() + "\n" + timeStream.toString()