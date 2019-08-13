package org.sabee.qrecc

import org.sabee.qrecc.util.{ ModFKV, Utility }

object Tang18 {
  def main(args: Array[String]): Unit = {
    var p = 1000
    val k = 126

    val A = Utility.textFile("src/test/resources/test_matrix4.txt")
    val (rowIndices, rowProbabilities, u, sigma) = ModFKV.modFKV(A, p, k)
    p = sigma.length

    // Proposition 6.14
    // \hat{V}^(i) is a column of \hat{V}.
    def V_ji(j: Int, i: Int): Double = {
      var out = 0.0
      for (x <- 0 until p) {
        val A_xj = A.query(rowIndices(x), j)
        val u_ix = u(i, x)
        out += A_xj * u_ix
      }
      val sigma_i = sigma(i)
      out / sigma_i
    }

    // The Ith and Jth entry we want to estimate
    val estI = 426284 // a specific row from the training set
    val estJ = 14407 // a specific column from the training set
    var out = 0.0
    for (x <- 0 until p) {
      val numSamples = 500
      var innerProduct = 0.0
      for (_ <- 0 until numSamples) {
        val sampledIdxValue = A.query(estI).sample()
        val Vij = V_ji(sampledIdxValue.index, x)
        innerProduct += Vij / sampledIdxValue.value
      }
      innerProduct /= numSamples
      innerProduct *= A.tilde.query(estI)

      val V_estJx = V_ji(estJ, x)
      out += innerProduct * V_estJx
    }
  }
}
