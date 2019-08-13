package org.sabee.qrecc.util

import org.sabee.qrecc.SQVector
import org.scalatest.FunSuite

class UtilitySuite extends FunSuite {
  test("textFile, test_matrix1") {
    val path = "src/test/resources/test_matrix1.txt"
    val sqm = Utility.textFile(path)

    // Verify row
    val actual = sqm.query(4).data
    val expected = SQVector(Seq(3), Seq(2.0)).data
    assert(actual === expected)

    // Verify SQVector of row norms
    val actualTilde = sqm.tilde.data
    val expectedTilde = SQVector(Seq(4), Seq(2.0)).data
    assert(actualTilde === expectedTilde)
  }

  test("textFile, test_matrix2") {
    val path = "src/test/resources/test_matrix2.txt"
    val sqm = Utility.textFile(path)

    // Verify row
    val actual = sqm.query(0).data
    val expected = SQVector(Seq(0, 2), Seq(1.0, 3.0)).data
    assert(actual === expected)

    // Verify SQVector of row norms
    val actualTilde = sqm.tilde.data
    val expectedTilde = SQVector(Seq(0), Seq(math.sqrt(10.0))).data
    assert(actualTilde === expectedTilde)
  }

  test("textFile, test_matrix3") {
    val path = "src/test/resources/test_matrix3.txt"
    val sqm = Utility.textFile(path)
    // Verify rows
    val actual = Seq(
      sqm.query(0).data,
      sqm.query(1).data,
      sqm.query(2).data,
    )
    val expected = Seq(
      SQVector(Seq(3), Seq(7.0)).data,          // row zero
      SQVector(Seq(0, 1), Seq(2.0, 3.0)).data,  // row one
      SQVector(Seq(2), Seq(4.0)).data,          // row two
    )
    for ((a,e) <- actual zip expected) assert(a === e)

    // Verify SQVector of row norms
    val actualTilde = sqm.tilde.data
    val expectedTilde = SQVector(Seq(0, 1, 2), Seq(7.0, math.sqrt(13.0), 4.0)).data
    assert(actualTilde === expectedTilde)
  }
}
