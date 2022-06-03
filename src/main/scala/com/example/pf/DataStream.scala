package com.example.pf

import breeze.plot.{Figure, Plot, plot}
import com.example.pf.model.{LinearGaussianObservationModel, LinearGaussianSystemModel}
import com.example.pf.model.{SystemModel, ObservationModel}

class DataStream():
    val enableX11 = true
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
        var selectedSystemModel: SystemModel = null
        var selectedObservationModel: ObservationModel = null
        var maximumLikelihood = Double.MinValue
        if enableX11 then
            f = Figure(s"Particle filter")
            f.width = 1480
            f.height = 740

        for
            i <- Range(-systemNoiseParameterRange, systemNoiseParameterRange, 1)
            j <- Range(-observationNoiseParameterRange, observationNoiseParameterRange, 1)
        do
          val a = math.pow(2.0, i) // variance of noise of the system model
          val b = math.pow(2.0, j) // variance of noise of the observation model
          if enableX11 then
            p0 = f.subplot(systemNoiseParameterRange*2, observationNoiseParameterRange*2,
              (i+systemNoiseParameterRange)*(observationNoiseParameterRange*2)+(j+observationNoiseParameterRange))
            p0.title = s"a=${a}, b=${b}"
            //val p1 = f.subplot(1, 2, 1)
            p0.legend = true
            //p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")

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
              val predict = x.mean
              if enableX11 then
                  p0 += plot(t0.toArray, y.toArray, name = "Input", style = '+', colorcode = "255,0,0")
                  p0 += plot(t.toArray, x.toArray, name = "predict particle", style = '.', colorcode = "0, 255, 0")
                  p0 += plot(t0.toArray, predict.toArray, name = "predict value", style = '+', colorcode = "0,0,255")

                  val l = filter.logLikelihood

                  likelihood += l
          println(s"logLikelyhood=${likelihood}(a=${a}, b=${b})")
          if likelihood > maximumLikelihood then
              maximumLikelihood = likelihood
              selectedSystemModel = systemModel
              selectedObservationModel = inversedObservationModel
        println(s"optimizing results: ${selectedSystemModel}, ${selectedObservationModel}")
    override def toString() =
        dataStream.toString() + "\n" + timeStream.toString()
