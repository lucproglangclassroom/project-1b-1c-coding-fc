package edu.luc.cs.cs371.topwords.core

import scala.collection.mutable

class WordStreamProcessor(
  howMany: Int,
  minLength: Int,
  windowSize: Int,
  everyKSteps: Int,
  minFrequency: Int,
  ignoreCase: Boolean,
  observer: WordCloudObserver
):

  require(howMany > 0)
  require(windowSize > 0)
  require(everyKSteps > 0)

  private val topWords = TopWords(howMany)
  private val window = mutable.Queue.empty[String]

  private var stepCount = 0

  def processWord(word: String): Unit =
    val normalized =
      if ignoreCase then word.toLowerCase
      else word

    if normalized.length >= minLength then
      window.enqueue(normalized)
      topWords.onWordAdded(normalized)
      stepCount += 1

      if window.size > windowSize then
        val removed = window.dequeue()
        topWords.onWordRemoved(removed)

      if window.size == windowSize && stepCount % everyKSteps == 0 then
        val cloud =
          topWords.topK().filter(_._2 >= minFrequency)

        observer.onCloudUpdated(cloud)
