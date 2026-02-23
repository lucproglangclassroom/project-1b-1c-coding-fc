package edu.luc.cs.cs371.topwords.core

final case class TopWords(
  howMany: Int,
  private val counts: Map[String, Int] = Map.empty
):
  require(howMany > 0)

  /** Returns a new TopWords with word count incremented. */
  def onWordAdded(word: String): TopWords =
    val newCount = counts.getOrElse(word, 0) + 1
    copy(counts = counts.updated(word, newCount))

  /** Returns a new TopWords with word count decremented (removes if drops below 1). */
  def onWordRemoved(word: String): TopWords =
    counts.get(word) match
      case None =>
        this
      case Some(c) =>
        val newCount = c - 1
        if newCount < 1 then copy(counts = counts - word)
        else copy(counts = counts.updated(word, newCount))

  /** Top K words by frequency desc, then alphabetically asc. */
  def topK(): Vector[(String, Int)] =
    counts.toVector
      .sortBy { case (w, f) => (-f, w) }
      .take(howMany)