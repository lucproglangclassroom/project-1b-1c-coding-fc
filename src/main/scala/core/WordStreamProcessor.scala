package edu.luc.cs.cs371.topwords.core

import scala.collection.immutable.Queue

object WordStreamProcessor:
  // Parameters controlling cloud generation
  final case class Config(
    cloudSize: Int,
    minLength: Int,
    windowSize: Int,
    everyKSteps: Int,
    minFrequency: Int,
    ignoreCase: Boolean
  )
  //Internal immutable state carried through the stream
  private final case class State(
    window: Queue[String],
    counts: Map[String, Int],
    steps: Int,
    emitted: Option[Seq[(String, Int)]]
  )
  
  // Turn an iterator of raw words into an iterator of clouds
  def clouds(wordsRaw: Iterator[String], cfg: Config): Iterator[Seq[(String, Int)]] =
    val initial = State(Queue.empty, Map.empty, 0, None)
    val states: Iterator[State] =
      wordsRaw.scanLeft(initial) { (st, word0) =>
        step(st, word0, cfg)
      }
    // Drop the initial state, then keep only states that emitted a cloud
    states
      .drop(1)
      .flatMap(_.emitted)
  // One pure transition step: given a state and a raw word, produce the next state
  private def step(st: State, wordRaw: String, cfg: Config): State =
    val word =
      if cfg.ignoreCase then wordRaw.toLowerCase
      else wordRaw
    // Ignore too-short words: no step increment, no window/counts change
    if word.length < cfg.minLength then
      st.copy(emitted = None)
    else
      val (window1, counts1) = addWord(st.window, st.counts, word)
      val (window2, counts2) = trimToWindow(cfg.windowSize, window1, counts1)
      val steps2 = st.steps + 1
      val emitted2 =
        if steps2 % cfg.everyKSteps == 0 then
          val cloud =
            counts2.toSeq
              .filter { case (_, c) => c >= cfg.minFrequency }
              .sortBy { case (w, c) => (-c, w) }
              .take(cfg.cloudSize)
          Some(cloud)
        else None
      State(window2, counts2, steps2, emitted2)
  // Add a word to the window and update counts accordingly
  private def addWord(
    window: Queue[String],
    counts: Map[String, Int],
    word: String
  ): (Queue[String], Map[String, Int]) =
    val window2 = window.enqueue(word)
    val counts2 = counts.updated(word, counts.getOrElse(word, 0) + 1)
    (window2, counts2)
  // Remove the oldest word if the window is too big, and update counts accordingly
  private def trimToWindow(
    windowSize: Int,
    window: Queue[String],
    counts: Map[String, Int]
  ): (Queue[String], Map[String, Int]) =
    if window.size <= windowSize then (window, counts)
    else
      val (removed, window2) = window.dequeue
      val newCount = counts(removed) - 1
      val counts2 =
        if newCount <= 0 then counts - removed
        else counts.updated(removed, newCount)
      (window2, counts2)
