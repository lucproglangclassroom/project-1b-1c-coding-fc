package edu.luc.cs.cs371.topwords.core

import scala.collection.mutable

class WordStreamProcessor(
    cloudSize: Int,
    minLength: Int,
    windowSize: Int,
    everyKSteps: Int,
    minFrequency: Int,
    ignoreCase: Boolean,
    observer: WordCloudObserver
):
  private val window = mutable.Queue.empty[String]
  private val counts = mutable.Map.empty[String, Int]
  private var steps = 0

  def processWord(wordRaw: String): Unit =
    val word = if ignoreCase then wordRaw.toLowerCase else wordRaw

    if word.length >= minLength then
      // add word to sliding window
      window.enqueue(word)
      counts(word) = counts.getOrElse(word, 0) + 1

      // remove old words if window exceeds windowSize
      if window.size > windowSize then
        val removed = window.dequeue()
        counts(removed) = counts(removed) - 1
        if counts(removed) <= 0 then counts.remove(removed): Unit

      // increment step count
      steps += 1

      // emit cloud every everyKSteps
      if steps % everyKSteps == 0 then
        val cloud =
          counts.toSeq
            .filter(_._2 >= minFrequency)
            .sortBy { case (w, c) => (-c, w) }
            .take(cloudSize)
        observer.onCloudUpdated(cloud)
