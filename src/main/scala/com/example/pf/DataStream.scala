package com.example.pf

import breeze.plot.{Figure, Plot, plot}
import com.example.pf.model.{LinearGaussianObservationModel, LinearGaussianSystemModel}
import com.example.pf.model.{SystemModel, ObservationModel}
import scala.collection.mutable.ListBuffer
import org.tinylog.Logger

class DataStream:
    class timeData:
        var time: Tensor = _
        def getTime = time
        var data: Tensor = _
        def getData = data
        var lastIndex = -1 // No data
        def push(t: Tensor, d: Tensor) =
          if lastIndex == -1 then
            val dimension = d.shape(0).toInt
            //println(d.reshape(1, d.shape(0).toInt))
            data = d.reshape(1, d.shape(0).toInt)
            //println(s"data=${data},${data.shape.toSeq}")
            time = t.reshape(1, t.shape(0).toInt).dup
            lastIndex = 0
          else
            //println(s"time push:${t} into ${time}")
            //println(s"data push:${d}")
            time.push(t)
            //println(s"results=${time}")
            data.push(d)

          this
        def multiplePush(t: Array[Int], d: Array[Double]): timeData =
            multiplePush(Tensor(t), Tensor(d))

        def multiplePush(t: Tensor, d: Tensor) =
          if lastIndex == -1 then
            val dimension = d.shape(0).toInt
            //println(d.reshape(dimension, 1))
            data = d.reshape(dimension, 1)
            //println(s"dataStream=${data},${data.shape.toSeq}")
            time = t.reshape(dimension, 1)
            lastIndex = 0
          else
            time.multiplePush(t)
            data.multiplePush(d)

          this
        override def toString() =
            var s = ""
            if time != null then
                s += time.toString()
            else
                s += "null"
            if data != null then
                s += "\n" + data.toString()
            else
                s += "\n" +"null"
            s

    var enableX11 = true
    var completeTimeSeries: Tensor = _
    var timeDataStream = timeData()
    var timePredictStream = timeData()
    var timeParticleStream = timeData()

    def tuning =
        //val systemNoiseParameterRange = 3
        //val observationNoiseParameterRange = 2
        //val systemNoiseParameterRange = Array(0.6)
        val systemNoiseParameterRange = Array(0.7)
        //val observationNoiseParameterRange = Array(2.0, 6.0, 8.0)
        val observationNoiseParameterRange = Array(8.0)
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
            //i <- Range(-systemNoiseParameterRange, systemNoiseParameterRange, 1)
            //j <- Range(-observationNoiseParameterRange, observationNoiseParameterRange, 1)
            i <- Range(0, systemNoiseParameterRange.length)
            j <- Range(0, observationNoiseParameterRange.length)
        do
          //val a = math.pow(2.0, i) // variance of noise of the system model
          //val b = math.pow(2.0, j) // variance of noise of the observation model
          val a = systemNoiseParameterRange(i)
          val b = observationNoiseParameterRange(j)
          if enableX11 then
            //p0 = f.subplot(systemNoiseParameterRange*2, observationNoiseParameterRange*2,
            //  (i+systemNoiseParameterRange)*(observationNoiseParameterRange*2)+(j+observationNoiseParameterRange))
            p0 = f.subplot(systemNoiseParameterRange.length, observationNoiseParameterRange.length,
                  i+j*systemNoiseParameterRange.length)
                 
            p0.title = s"a=${a}, b=${b}"
            //val p1 = f.subplot(1, 2, 1)
            p0.legend = true
            //p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")
            p0 += plot(timeDataStream.time.toArray, timeDataStream.data.toArray, name = "Input", style = '-', colorcode = "255,0,0")

          Logger.debug("a={}, b={}", a, b)
          val systemModel = LinearGaussianSystemModel(0.0, a)
          val inversedObservationModel = LinearGaussianObservationModel(0.0, b)
          val filter = ParticleFilter(systemModel, inversedObservationModel)
          filter.debug = true
          val timeIter = timeDataStream.getTime.tensorIterator
          val dataIter = timeDataStream.getData.tensorIterator
          val startTime = timeIter.next()
          val startData = dataIter.next()
          var x = Tensor.repeat(startData, 20)
          var likelihood = 0.0
          val timePredictStreamCandidate = timeData()
          val timeParticleStreamCandidate = timeData()
          val completeTimeIter = completeTimeSeries.doubleIterator
          while dataIter.hasNext && completeTimeIter.hasNext && x != null do
              //println(s"timePredictStreamCandidate=${timePredictStreamCandidate}")
              val y = dataIter.next()
              var t0 = timeIter.next()
              var completeTime = completeTimeIter.next()
              //println(s"t0=${t0}, timePredictStreamCandidate=${timePredictStreamCandidate}")
              if completeTime == t0(0) then
                  if completeTime.toInt % (60*120) == 0 then
                      Logger.debug("completeTime={}, t0={}(predictOnly=false interval)", completeTime, t0(0))
                  x = filter.step(x, y)
                  if x != null then
                      val t = Tensor.repeat(t0, x.length.toInt)
                      val predict = x.mean
                      timePredictStreamCandidate.push(t0, predict)
                      //Logger.debug("push; time length becomes={}", timePredictStreamCandidate.time.length)
                      timeParticleStreamCandidate.multiplePush(t, x)

                      val l = filter.logLikelihood

                      likelihood += l
                      //Logger.debug("now, likelihood={}", likelihood)
                  else // Illegal case
                      Logger.error("filter step was failed: x={}", x)
              else
                  while completeTime != t0(0) do
                      if completeTime.toInt % (60*120) == 0 then
                          Logger.debug("completeTime={}, t0={}(predictOnly interval)", completeTime, t0(0))
                      x = filter.step(x, y, predictOnly = true)
                      if x != null then
                          val t = Tensor.repeat(completeTime, x.length.toInt)
                          val predict = x.mean
                          timePredictStreamCandidate.push(Tensor(completeTime), predict)
                          //Logger.debug("push; time length becomes={}", timePredictStreamCandidate.time.length)
                          timeParticleStreamCandidate.multiplePush(t, x)

                          //val l = filter.logLikelihood

                          //likelihood += l
                          //Logger.debug("now, likelihood={}", likelihood)
                      else // Illegal case
                          Logger.error("filter step was failed: x={}", x)
                      completeTime = completeTimeIter.next()
                  // completedTime == t0(0)
                  if completeTime.toInt % (60*120) == 0 then
                      Logger.debug("completeTime={}, t0={}(predictOnly=false interval; after one-step predictonly interval)", completeTime, t0(0))
                  x = filter.step(x, y)
                  if x != null then
                      val t = Tensor.repeat(t0, x.length.toInt)
                      val predict = x.mean
                      timePredictStreamCandidate.push(t0, predict)
                      //Logger.debug("push; time length becomes={}", timePredictStreamCandidate.time.length)
                      timeParticleStreamCandidate.multiplePush(t, x)

                      val l = filter.logLikelihood

                      likelihood += l
                      //Logger.debug("now, likelihood={}", likelihood)
                  else // Illegal case
                      Logger.error("filter step was failed: x={}", x)
          //if enableX11 && x != null then
          if enableX11 && timePredictStreamCandidate.time.length>0 then
                Logger.debug("time length={}", timePredictStreamCandidate.time.length)
                Logger.debug("data length={}", timePredictStreamCandidate.data.length)
                p0 += plot(timePredictStreamCandidate.time.toArray, timePredictStreamCandidate.data.toArray, name = "predict", style = '+', colorcode = "0, 255, 0")
                p0 += plot(timeParticleStreamCandidate.time.toArray, timeParticleStreamCandidate.data.toArray, name = "particle", style = '.', colorcode = "0, 0, 255")
          Logger.debug("logLikelyhood={}(a={}, b={})", likelihood, a, b)
          if x!=null && likelihood > maximumLikelihood then
              maximumLikelihood = likelihood
              selectedSystemModel = systemModel
              selectedObservationModel = inversedObservationModel
              timePredictStream = timePredictStreamCandidate
              timeParticleStream = timeParticleStreamCandidate
        Logger.info("optimizing results: {}, {}", selectedSystemModel, selectedObservationModel)

    def timePredictStreamSampling(period: Double) =
        if timePredictStream.time.x == null then
            Logger.error("timrePredictStream.time=={}", null)
            timeData()
        else
            sampling(timePredictStream, period)
    def sampling(stream: timeData, period: Double = 15) =
        var timeSampleStream = timeData()
        val iter = stream.time.toArray.zip(stream.data.toArray).iterator
        var nextBuffer = null

        var windowStart = iter.next
        var timeWindow = ListBuffer.empty[Double]
        var dataWindow = ListBuffer.empty[Double]
        var i = windowStart

        var sequenceEnd = false
        while !sequenceEnd do
            while
                i._1 - windowStart._1 < period && !sequenceEnd do 
                //Logger.debug(" append:${}", i)
                timeWindow += i._1
                dataWindow += i._2
                if iter.hasNext then
                    i = iter.next
                else
                    sequenceEnd = true
                    //println(s"Now, sequenceEnd=true")

            //println(s" windowStart=${windowStart._1}")
            //println(s" i=${i._1}")
            //println(s"push: ${timeWindow}, ${dataWindow}")
            timeSampleStream.push(Tensor(timeWindow.head), Tensor(Array(dataWindow.max, dataWindow.min)))
            timeWindow.clear
            dataWindow.clear
            windowStart = i
            //println(s"now, windowStart=${windowStart._1}")
        if enableX11 then
            val f = Figure(s"Particle filter")
            f.width = 1480
            f.height = 740
            val p0 = f.subplot(1, 1, 0)
            p0.title = s"Particle filter prediction"
            p0.legend = true
            p0 += plot(timePredictStream.time.toArray, timePredictStream.data.toArray, name = "predict", colorcode = "0, 255, 0")
            p0 += plot(timeSampleStream.time.toArray, timeSampleStream.data.getColumn(0).toArray, name = "samplng max", style = '+', colorcode = "255, 0, 0")
            p0 += plot(timeSampleStream.time.toArray, timeSampleStream.data.getColumn(1).toArray, name = "samplng min", style = '+', colorcode = "255, 255, 0")
        timeSampleStream
            
    var timeStateStream = timeData()


    override def toString() =
        timeDataStream.data.toString() + "\n" + timeDataStream.time.toString()
