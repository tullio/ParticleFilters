package com.example.pf

import breeze.plot.{Figure, Plot, plot}
import com.example.pf.model.{LinearGaussianObservationModel, LinearGaussianSystemModel}
import com.example.pf.model.LinearCauchySystemModel
import com.example.pf.model.{SystemModel, ObservationModel}
import scala.collection.mutable.ListBuffer
import org.tinylog.Logger

import scala.collection.parallel.CollectionConverters.*

class DataStream(step: Int = 0):

    class timeData:
        var time: Tensor = _
        def getTime = time
        var data: Tensor = _
        def getData = data
        var lastIndex = -1 // No data
        def push(t: Tensor, d: Tensor) =
          if lastIndex == -1 then
            val dimension = d.shape(0).toInt
            //Logger.tags("DEBUG").debug(d.reshape(1, d.shape(0).toInt))
            data = d.reshape(1, d.shape(0).toInt)
            //Logger.tags("DEBUG").debug("data={},{}",data,data.shape.toSeq)
            time = t.reshape(1, t.shape(0).toInt).dup
            lastIndex = 0
          else
            //Logger.tags("DEBUG").debug("time push:{} into {}",t, time)
            //println(s"data push:${d}")
            time.push(t)
            //Logger.tags("DEBUG").debug("results={}", time)
            data.push(d)

          this
        def multiplePush(t: Array[Int], d: Array[Double]): timeData =
            multiplePush(Tensor(t), Tensor(d))

        def multiplePush(t: Tensor, d: Tensor, debug: Boolean = false) =
          if lastIndex == -1 then
            val dimension = d.shape(0).toInt
            //println(d.reshape(dimension, 1))
            data = d.reshape(dimension, 1)
            if debug then
                Logger.tags("DEBUG").debug("dataStream={},{}",data, data.shape.toSeq)
            time = t.reshape(dimension, 1)
            lastIndex = 0
          else
            if debug then
                Logger.tags("DEBUG").debug("time={} -->", time)
            time.multiplePush(t)
            if debug then
                Logger.tags("DEBUG").debug("---> time={}", time)
                Logger.tags("DEBUG").debug("data = {} -->", data)
            data.multiplePush(d)
            if debug then
                Logger.tags("DEBUG").debug("---> data = {}", data)

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

/**
    class timeData:
        var time = scala.collection.mutable.ArrayBuffer.empty[Int]
        def getTime = time
        var data = scala.collection.mutable.ArrayBuffer.empty[Double]
        def getData = data
        var lastIndex = -1 // No data
        def push(t: Tensor, d: Tensor) =
          if lastIndex == -1 then
            val dimension = d.shape(0).toInt
            //println(d.reshape(1, d.shape(0).toInt))
            if d.shape == Array(1L) then
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
  * */
    val prop = readProperties
    val numParticles = prop.get("NumParticles").asInstanceOf[Int]
    var enableX11 = false
    /**
      * 欠損のない，0からデータの個数までの秒を表す数列
      * */
    var completeTimeSeries: Tensor = _
    /**
      * 入力データ
      * */
    var timeDataStream = timeData()
    /**
      * tuningを呼ぶとセットされる予測データ
    * */
    var timePredictStream = timeData()
    /**
      * tuningを呼ぶとセットされる粒子データ
    * */
    var timeParticleStream = timeData()
    /**
      * timeDataStreamに粒子フィルタを適用し，尤度最大になったデータを
      * timePredictStreamとtimeParticleStreamにセットする．
      * 結果の画像データも生成してs"ParticleFilter-${step}.png"として保存する．
      * 予めcompleteTimeSeriesのセットが必要．
     * */
    def tuning =
        Logger.tags("DEBUG").debug("Start tuning...", "")
        //val systemNoiseParameterRange = 3
        //val observationNoiseParameterRange = 2
        val systemNoiseParameterRange = Array(2.0, 4.0, 8.0)
        //val systemNoiseParameterRange = Array(3.0)
        //val systemNoiseParameterRange = Array(0.01)
        val observationNoiseParameterRange = Array(6.0, 8.0)
        //val observationNoiseParameterRange = Array(8.0)
        var f: Figure = null
        var p0: Plot = null
        var selectedSystemModel: SystemModel = null
        var selectedObservationModel: ObservationModel = null
        var maximumLikelihood = Double.MinValue
        if enableX11 then
            f = Figure(s"Particle filter")
            f.width = 1480
            f.height = 740
        Logger.tags("DEBUG").debug("Start optimization...", "")
        /*
        for
            //i <- Range(-systemNoiseParameterRange, systemNoiseParameterRange, 1)
            //j <- Range(-observationNoiseParameterRange, observationNoiseParameterRange, 1)
            i <- Range(0, systemNoiseParameterRange.length)
            j <- Range(0, observationNoiseParameterRange.length)
        do
         */
        Range(0, systemNoiseParameterRange.length).par.foreach{i =>
            Range(0, observationNoiseParameterRange.length).toArray.foreach{j =>
                //val a = math.pow(2.0, i) // variance of noise of the system model
                //val b = math.pow(2.0, j) // variance of noise of the observation model
                val a = systemNoiseParameterRange(i)
                val b = observationNoiseParameterRange(j)
                if enableX11 then
                  //p0 = f.subplot(systemNoiseParameterRange*2, observationNoiseParameterRange*2,
                  //  (i+systemNoiseParameterRange)*(observationNoiseParameterRange*2)+(j+observationNoiseParameterRange))
                  p0 = f.subplot(systemNoiseParameterRange.length, observationNoiseParameterRange.length,
                        //i+j*systemNoiseParameterRange.length)
                        j+i*observationNoiseParameterRange.length)

                  p0.title = s"system=${a}, observation=${b}"
                  //val p1 = f.subplot(1, 2, 1)
                  p0.legend = true
                  //p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")
                  p0 += plot(timeDataStream.time.toArray, timeDataStream.data.toArray, name = "Input", style = '-', colorcode = "255,0,0")

                Logger.tags("INFO").info("system={}, ovservation={}", a, b)
                val systemModel = LinearGaussianSystemModel(0.0, a)
                //val systemModel = LinearCauchySystemModel(0.0, a)
                val inversedObservationModel = LinearGaussianObservationModel(0.0, b)
                val filter = ParticleFilter(systemModel, inversedObservationModel)
                filter.debug = true
                val timeIter = timeDataStream.getTime.tensorIterator
                val dataIter = timeDataStream.getData.tensorIterator
                val startTime = timeIter.next()
                val startData = dataIter.next()
                Logger.debug("startData={}, numParticles={}", startData, numParticles)
                //val x = Tensor.repeat(startData, numParticles)
                var x = Tensor.repeat(startData, numParticles)
                var likelihood = 0.0
                val timePredictStreamCandidate = timeData()
                val timeParticleStreamCandidate = timeData()
                val completeTimeIter = completeTimeSeries.doubleIterator
                Logger.tags("DEBUG").debug("Start filtering steps", "")
                while dataIter.hasNext && completeTimeIter.hasNext && x != null do
                    //Logger.tags("DEBUG").debug("Start one filtering step. x={}", x)
                    //println(s"timePredictStreamCandidate=${timePredictStreamCandidate}")
                    val y = dataIter.next()
                    var t0 = timeIter.next()
                    var completeTime = completeTimeIter.next()
                    //println(s"t0=${t0}, timePredictStreamCandidate=${timePredictStreamCandidate}")
                    def executeStep =
                        //Logger.tags("DEBUG").debug("x={}", x)
                        val step = filter.step(x, y)
                        //Logger.tags("DEBUG").debug("step={}", step)
                        //x.x = step.x//.dup
                        x = step
                        //Logger.tags("DEBUG").debug("step->x={}", x)
                    if completeTime == t0(0) then
                        if completeTime.toInt % (60*120) == 0 then
                            Logger.debug("completeTime={}, t0={}(predictOnly=false interval)", completeTime, t0(0))
                            Logger.debug("x={}, y={}. lets filtering", x, y)
                        //x = filter.step(x, y)
                        executeStep
                        if completeTime.toInt % (60*120) == 0 then
                            Logger.debug("now x={}.", x)
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
                        if completeTime.toInt % (60*120) == 0 then
                            Logger.debug("completeTime={}, t0={}", completeTime, t0)
                        while completeTime != t0(0) && x != null do
                            //Logger.debug("completeTime={}, t0={}", completeTime, t0)
                            if completeTime.toInt % (60*5) == 0 then
                                if completeTime.toInt % (60*120) == 0 then
                                    Logger.debug("completeTime={}, t0={}(predictOnly interval; but proceeds resampling!)", completeTime, t0(0))
                                executeStep
                            else
                                if completeTime.toInt % (60*120) == 0 then
                                    Logger.debug("completeTime={}, t0={}(predictOnly interval)", completeTime, t0(0))
                                    Logger.debug("x={}, y={}. lets filtering with predictOnly", x, y)
                                executeStep
                            if completeTime.toInt % (60*120) == 0 then
                                Logger.debug("now x={}.", x)
                            if x != null then
                                val t = Tensor.repeat(completeTime, x.length.toInt)
                                val predict = x.mean
                                timePredictStreamCandidate.push(Tensor(completeTime), predict)
                                //Logger.debug("push; time length becomes={}", timePredictStreamCandidate.time.length)
                                //Logger.tags("DEBUG").debug("t size={}, x size={}", t.shape.toSeq, x.shape.toSeq)
                                timeParticleStreamCandidate.multiplePush(t, x)

                                //val l = filter.logLikelihood

                                //likelihood += l
                                //Logger.debug("now, likelihood={}", likelihood)
                            else // Illegal case
                                Logger.tags("NOTICE", "INFO", "DEBUG").error("filter step was failed: x={}", x)
                            completeTime = completeTimeIter.next()
                        // completedTime == t0(0)
                        if completeTime.toInt % (60*120) == 0 then
                            Logger.debug("completeTime={}, t0={}(predictOnly=false interval; after one-step predictonly interval)", completeTime, t0(0))
                        if x != null then
                            executeStep
                        if x != null then

                            val t = Tensor.repeat(t0, x.length.toInt)
                            val predict = x.mean
                            //Logger.tags("NOTICE", "INFO", "DEBUG").error("filter step was succeeded?: x={}", x)
                            timePredictStreamCandidate.push(t0, predict)
                            //Logger.debug("push; time length becomes={}", timePredictStreamCandidate.time.length)
                            //Logger.tags("NOTICE", "INFO", "DEBUG").error("filter step was succeeded??: x={}", x)
                            timeParticleStreamCandidate.multiplePush(t, x)

                            val l = filter.logLikelihood

                            likelihood += l
                            //Logger.debug("now, likelihood={}", likelihood)
                            //Logger.tags("NOTICE", "INFO", "DEBUG").error("filter step was succeeded: x={}", x)
                            //Logger.tags("NOTICE", "INFO", "DEBUG").info("filter step was succeeded", "")
                        else // Illegal case
                            Logger.tags("NOTICE", "INFO", "DEBUG").error("filter step was failed", "")
                        //Logger.tags("DEBUG").debug("End one filtering step execution. current x={}({})", x, x.shape.toSeq)
                    /*
                    if x != null then
                        Logger.tags("DEBUG").debug("End one filtering step. current x={}({})", x, x.shape.toSeq)
                    else
                        Logger.tags("DEBUG").debug("End one filtering step. current x={}", x)
                     */
                Logger.tags("DEBUG").debug("End filtering steps", "")
                //if enableX11 && x != null then
                if enableX11 && timePredictStreamCandidate.time.length>0 then
                      Logger.debug("time length={}", timePredictStreamCandidate.time.length)
                      Logger.debug("data length={}", timePredictStreamCandidate.data.length)
                      p0 += plot(timePredictStreamCandidate.time.toArray, timePredictStreamCandidate.data.toArray, name = "predict", style = '+', colorcode = "0, 255, 0")
                      p0 += plot(timeParticleStreamCandidate.time.toArray, timeParticleStreamCandidate.data.toArray, name = "particle", style = '.', colorcode = "0, 0, 255")
                Logger.tags("DEBUG", "INFO").info("logLikelyhood={}(a={}, b={})", likelihood, a, b)
                if x!=null && likelihood > maximumLikelihood then
                    maximumLikelihood = likelihood
                    selectedSystemModel = systemModel
                    selectedObservationModel = inversedObservationModel
                    timePredictStream = timePredictStreamCandidate
                    timeParticleStream = timeParticleStreamCandidate
            }
        }
        Logger.tags("DEBUG").debug("End optimization...", "")
        Logger.tags("NOTICE", "INFO", "DEBUG").info("optimizing results: {}, {}", selectedSystemModel, selectedObservationModel)
        if enableX11 then
            f.saveas(s"ParticleFilter-${step}.png")

            

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
