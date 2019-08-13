package org.sabee.qrecc

import org.scalatest.FunSuite
import org.sabee.qrecc.util.Utility._

class SQVectorSuite extends FunSuite {
  test("SQVector initializes") {
    val indices = Seq(0, 3, 4, 6)
    val values = Seq(3.0, 2.0, 1.0, 2.0)
    val norm = l2norm(values)
    val expected: Seq[SQVectorEntry] = Seq(
      SQVectorEntry(0, 3, 9 / norm),
      SQVectorEntry(3, 2, 13 / norm),
      SQVectorEntry(4, 1, 14 / norm),
      SQVectorEntry(6, 2, 18 / norm),
    )
    val actual = SQVector(indices, values).data
    assert(actual equals expected)
  }

  test("SQVector query") {
    val indices = Seq(0, 3, 4, 6)
    val values = Seq(3.0, 2.0, 1.0, 2.0)
    val sqv = SQVector(indices, values)

    // These exist in indices. They should return a nonzero value.
    assert(sqv.query(0) equals 3.0)
    assert(sqv.query(3) equals 2.0)
    assert(sqv.query(4) equals 1.0)
    assert(sqv.query(6) equals 2.0)

    // These don't exist in indices. They should return zero.
    assert(sqv.query(1) equals 0.0)
    assert(sqv.query(2) equals 0.0)
  }

  test("SQVector sample specific value") {
    val indices = Seq(0, 3, 4, 6)
    val values = Seq(3.0, 2.0, 1.0, 2.0)
    val norm = l2norm(values)
    val sqv = SQVector(indices, values)

    for (i <- 0 to 9) assert(sqv.sample(i / norm) === IndexValue(0, 3.0))
    for (i <- 10 to 13)
      assert(sqv.sample(i / norm) === IndexValue(3, 2.0), s"The target is $i / $norm}")
    for (i <- 14 to 14)
      assert(sqv.sample(i / norm) === IndexValue(4, 1.0), s"The target is $i / $norm}")
    for (i <- 15 to 18)
      assert(sqv.sample(i / norm) === IndexValue(6, 2.0), s"The target is $i / $norm}")
  }
}
