package org.sabee.qrecc

import scala.annotation.tailrec
import scala.math.pow
import org.sabee.qrecc.util.Utility

case class SQVectorEntry(index: Index, value: Value, probability: Probability)
case class IndexValue(index: Index, value: Value)

class SQVector private (val data: Seq[SQVectorEntry], val l2norm: Double) {

  // We binary search for the target. If the index is found, we return
  // the corresponding value. If not, we return 0.
  def query(target: Int): Double = {
    @tailrec
    def bs(start: Int, end: Int): Double = {
      if (start > end) return 0.0
      val mid = start + (end - start + 1) / 2
      data match {
        case arr if arr(mid).index == target => arr(mid).value
        case arr if arr(mid).index > target  => bs(start, mid - 1)
        case arr if arr(mid).index < target  => bs(mid + 1, end)
      }
    }
    bs(0, data.length - 1)
  }

  def sample(target: Probability = Utility.nextDouble()): IndexValue = {
    @tailrec
    def bs(start: Int, end: Int): IndexValue = {
      if (end - start <= 1) {
        return IndexValue(data(end).index, data(end).value)
      }
      val mid = start + (end - start + 1) / 2
      data match {
        case arr if arr(mid).probability == target => IndexValue(arr(mid).index, arr(mid).value)
        case arr if arr(mid).probability > target  => bs(start, mid - 1)
        case arr if arr(mid).probability < target  => bs(mid + 1, end)
      }
    }
    if (data.head.probability >= target) IndexValue(data.head.index, data.head.value)
    else bs(0, data.length - 1)
  }
}

object SQVector {
  // We assume that the indices are in ascending order.
  def apply(indices: Seq[Index], values: Seq[Value]): SQVector = {
    val norm: Value = Utility.l2norm(values)
    var accum = 0.0
    val data = for ((index, value) <- indices zip values) yield {
      accum += pow(value, 2)
      SQVectorEntry(index, value, accum / norm)
    }
    new SQVector(data, norm)
  }
}
