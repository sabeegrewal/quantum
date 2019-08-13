package org.sabee.qrecc.util

import breeze.linalg.{ CSCMatrix, DenseMatrix, DenseVector, svd }

import scala.math.{ pow, sqrt }
import org.sabee.qrecc._

import scala.collection.immutable

object ModFKV {
  private var startTime = 0L
  private def startTimer(): Unit = { startTime = System.nanoTime }
  def stopTimer(msg: String, end: Long = System.nanoTime): Unit = {
    println(s"${end - startTime}\t\t<-- $msg")
  }

  // Takes an m x n SQMatrix and returns a p x n SQMatrix
  def modFKV(sqm: SQMatrix, p: Int, k: Int) = {
    // Rows
    startTimer()
    val rowIndices = for (_ <- 0 until p) yield sqm.sampleRowIndex()
    val rowProbabilities = for (i <- rowIndices)
      yield pow(sqm.tilde.query(i), 2) / sqm.tilde.l2norm
    stopTimer("Rows")

    // Columns
    startTimer()
    val colIndices = for {
      _ <- 0 until p
      s = Utility.nextInt(p)
    } yield sqm.query(rowIndices(s)).sample().index
    val colProbabilities = colIndices.par.map { j =>
      {
        rowIndices.foldLeft(0.0)((a, v) => {
          val sqv = sqm.query(v)
          a + pow(sqv.query(j), 2) / sqv.l2norm
        })
      }
    }
    stopTimer("Cols")

    // Assemble W
    startTimer()
    val builder = new CSCMatrix.Builder[Value](rows = p, cols = p)
    for ((row, rowIndex) <- rowIndices.par.zipWithIndex.par) {
      for ((col, colIndex) <- colIndices.par.zipWithIndex.par) {
        val value = sqm.query(row, col) / sqrt(p * rowProbabilities(rowIndex)) / sqrt(
          p * colProbabilities(colIndex))
        synchronized(
          builder.add(rowIndex, colIndex, value)
        )
      }
    }
    val W = builder.result()
    stopTimer("W")

    // SVD
    startTimer()
    val x = svd(W, k)
    stopTimer("SVD")

    val U = x.leftVectors
    val Sigma = x.singularValues
    (rowIndices, rowProbabilities, U, Sigma)
  }
}
