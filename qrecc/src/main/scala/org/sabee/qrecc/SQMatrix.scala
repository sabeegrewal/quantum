package org.sabee.qrecc

import scala.math.sqrt
import org.sabee.qrecc.util.Utility

class SQMatrix private (val matrix: Map[Row, SQVector], val tilde: SQVector) {

  def query(row: Row): SQVector = {
    matrix(row)
  }

  def query(row: Row, col: Col): Value = {
    query(row).query(col)
  }

  def sampleRowIndex(target: Probability = Utility.nextDouble()): Index = {
    tilde.sample().index
  }
}

object SQMatrix {
  def apply(matrix: Map[Row, SQVector]): SQMatrix = {
    val rowAndNorm: Seq[(Row, Double)] = (for {
      (row, sqv) <- matrix
    } yield (row, sqv.l2norm)).toSeq
    val (row, norm) = rowAndNorm.sortBy(_._1).unzip
    val tilde = SQVector(row, norm.map(x => sqrt(x)))
    new SQMatrix(matrix, tilde)
  }
}
