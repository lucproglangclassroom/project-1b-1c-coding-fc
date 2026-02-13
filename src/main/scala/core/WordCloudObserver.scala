package edu.luc.cs.cs371.topwords.core

trait WordCloudObserver:
  def onCloudUpdated(cloud: Seq[(String, Int)]): Unit
