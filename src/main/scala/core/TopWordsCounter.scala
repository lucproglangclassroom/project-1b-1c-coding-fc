package edu.luc.cs.cs371.topwords.core
import scala.collection.mutable

class TopWords (
  howMany: Int
):
  // Invariant: howMany > 0 (Cloud size must be positive)
  require(howMany > 0)

  // Core data structure holding words and their frequency; mutable for efficiency, and encapsulated for safety
  private val counts = mutable.HashMap.empty[String, Int]

  // Whenever a new word is added...
  def onWordAdded(word: String): Unit =
    // Increment the count for the word, or initialize it to 1 if it's new
    val newCount = counts.getOrElse(word, 0) + 1
    // Update the count in the map
    counts.update(word, newCount)

  //Whenever a word is removed...
  def onWordRemoved(word: String): Unit =
    // Check if the word exists in the map
    counts.get(word) match
      // If the word to be removed is not in the map, do nothing
      case None =>
        ()
      // If the word exists...
      case Some(c) =>
        // Decrement the count for the word
        val newCount = c - 1
        // If the new count is less than 1, remove the word from the map; otherwise, update the map with the new count
        if newCount < 1 then counts.remove(word)
        else counts.update(word, newCount)

  // Providing structured output of the top K words and their frequencies
  def topK(): Vector[(String, Int)] =
    // Ordered by frequency (descending) and then alphabetically (ascending), and take the top K entries
    counts.toVector
      .sortBy { case (w, f) => (-f, w) }
      .take(howMany)

// Companion object for formatting the output of the top words
object TopWordsCounter:
  def formatCloud(items: Seq[(String, Int)]): String =
    items.map { case (w, f) => s"$w: $f" }.mkString(" ")
