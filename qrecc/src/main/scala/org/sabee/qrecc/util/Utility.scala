package org.sabee.qrecc.util

import scala.math.pow
import org.sabee.qrecc._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

object Utility {
  val seed = 42
  val r = new Random(seed)

  def nextDouble(): Double = {
    r.nextDouble()
  }

  def nextInt(upperBound: Int): Int = {
    r.nextInt(upperBound)
  }

  def l2norm(values: Seq[Value]): Value = {
    values.fold(0.0)((a, v) => a + pow(v, 2))
  }

  def textFile(path: String): SQMatrix = {
    val source = io.Source.fromFile(path)
    val matrix: mutable.Map[Row, (ListBuffer[Index], ListBuffer[Value])] = mutable.Map.empty
    for (line <- source.getLines()) {
      val parts = line.split("\t").map(_.trim)
      val (row, col, value) = (parts(0).toInt, parts(1).toInt, parts(2).toDouble)
      matrix
        .get(row) match {
        case Some(e) => {
          matrix(row) = (e._1 :+ col, e._2 :+ value)
        }
        case None => matrix.put(row, (ListBuffer(col), ListBuffer(value)))
      }
    }
    SQMatrix(matrix.toMap map {
      case (row, lists) => row -> SQVector(lists._1.toList, lists._2.toList)
    })
  }
}
