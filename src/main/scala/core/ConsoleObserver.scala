package edu.luc.cs.cs371.topwords.app

import edu.luc.cs.cs371.topwords.core.*
import edu.luc.cs.cs371.topwords.core.TopWordsCounter

class ConsoleObserver extends WordCloudObserver:

  override def onCloudUpdated(cloud: Seq[(String, Int)]): Unit =
    try
      println(TopWordsCounter.formatCloud(cloud))
    catch
      case _: java.io.IOException =>
        ()
