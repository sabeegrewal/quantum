package org.sabee.qrecc

import scala.math.sqrt

import org.scalatest.FunSuite

import org.sabee.qrecc.util.Utility.l2norm

class SQMatrixSuite extends FunSuite {
  test("SQMatrix initializes") {
    // Make a 2x3 matrix
    val rowOneIndices = Seq(2, 3, 7)
    val rowOneValues = Seq(1.0, 2.0, 2.0)
    val rowTwoIndices = Seq(1, 2, 9)
    val rowTwoValues = Seq(5.0, 5.0, 2.0)
    val sqm = SQMatrix(
      Map(
        0 -> SQVector(rowOneIndices, rowOneValues),
        1 -> SQVector(rowTwoIndices, rowTwoValues)
      ))
    val tildeIndices = Seq(0, 1)
    val tildeValues = Seq(sqrt(l2norm(rowOneValues)), sqrt(l2norm(rowTwoValues)))
    val expected = SQVector(tildeIndices, tildeValues).data
    val actual = sqm.tilde.data
    assert(expected === actual)
  }

  test("SQMatrix row query") {
    val rowOneIndices = Seq(2, 3, 7)
    val rowOneValues = Seq(1.0, 2.0, 2.0)
    val rowTwoIndices = Seq(1, 2, 9)
    val rowTwoValues = Seq(5.0, 5.0, 2.0)
    val sqm = SQMatrix(
      Map(
        0 -> SQVector(rowOneIndices, rowOneValues),
        1 -> SQVector(rowTwoIndices, rowTwoValues)
      ))
    val actual1 = sqm.query(0).data
    val actual2 = sqm.query(1).data
    val expected1 = SQVector(rowOneIndices, rowOneValues).data
    val expected2 = SQVector(rowTwoIndices, rowTwoValues).data
    assert(actual1 === expected1)
    assert(actual2 === expected2)
  }

  test("SQMatrix value query") {
    val rowOneIndices = Seq(2, 3, 7)
    val rowOneValues = Seq(1.0, 2.0, 2.0)
    val rowTwoIndices = Seq(1, 2, 9)
    val rowTwoValues = Seq(5.0, 5.0, 2.0)
    val sqm = SQMatrix(
      Map(
        0 -> SQVector(rowOneIndices, rowOneValues),
        1 -> SQVector(rowTwoIndices, rowTwoValues)
      ))
    assert(sqm.query(0, 2) === 1.0)
    assert(sqm.query(1, 2) === 5.0)
  }
}
