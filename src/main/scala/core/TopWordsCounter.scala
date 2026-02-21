package edu.luc.cs.cs371.topwords.core

object TopWordsCounter:
  /** Formats a cloud as: "word1: n1 word2: n2 ..." */
  def formatCloud(items: Seq[(String, Int)]): String =
    items.map { case (w, f) => s"$w: $f" }.mkString(" ")
