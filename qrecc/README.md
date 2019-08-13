# Quantum-inspired classical recommendation system

This project contains an implementation of the recommendation system detailed in Ewin 
Tang's undergraduate thesis, _[A quantum-inspired classical algorithm for recommendation
systems](https://arxiv.org/pdf/1807.04271.pdf)_. More information can be found on 
[Ewin's blog](https://ewintang.com/blog/2019/01/28/an-overview-of-quantum-inspired-sampling/)
and in this [recent paper](https://arxiv.org/pdf/1905.10415.pdf) on quantum-inspired algorithms. 

The data structures are implemented in `SQVector` and `SQMatrix` (SQ = "Sample" and "Query"). The
main algorithm is implemented in `Tang18`. The user
must provide their own dataset. 

This code also includes improvements to the original data structure which are detailed 
in the next section.

## Data Structure Improvements

Here we compare the data structure in our implementation to the data structure detailed 
in _[A quantum-inspired classical algorithm for recommendation
systems](https://arxiv.org/pdf/1807.04271.pdf)_. 

**Storing a _n_ dimensional vector with _w_ nonzero entries.** 

| | Paper | Our Implementation | 
| --------  | ----- | -------------- |
| Space Complexity | `O(w log(n))` | `O(w)` |
| Query Access | `O(log(n))` | `O(log(w)) ` |
| Sample Vector Entry | `O(log(n))` | `O(log(w))` | 
| l2 Norm Access | `O(1)` | `O(1)` |


**Storing an _m_ x _n_ matrix with _w_ nonzero entries.**

| | Paper | Our Implementation | 
| --------  | ----- | -------------- |
| Space Complexity | `O(w log(mn))` | `O(wm) `  |  
| Query Access | `O(log(mn))` | `O(log(w))` |
| l2 Norm Access | `O(log(m))` | `O(1) ` |
| Frobenius Norm Access | `O(1)` |`O(1)` |
| Sample Matrix Entry | `O(log(mn))` | `O(log(m) + log(w))` |
| Sample Matrix Row  | `O(log(mn))` | `O(log(m))` |


### Vector 

The algorithm requires a data structure that stores _w_ nonzero entries of an _n_ dimensional vector 
with fast sample and query access. We do this by storing a tuple for each nonzero entry
in the following format:

```
(index, value, accum / norm)
```

where `index` and `value` correspond to the index and value in the original vector, 
`norm` is the l2 norm of the vector, and `accum` is the sum of the square of all
values that have been added to the data structure. 

As an example, say we need to store a vector `v := (v0 = 1.0, v1 = 0.0, v2 = 4.0, v3 = 3.0)`.
Our data structure would have the following tuples:

```
(0, 1.0, 1/26)  // v0
(2, 4.0, 17/26) // v2
(3, 3.0, 26/26) // v3
```

Note that we only store the nonzero entries. Let `indices` and `values` be the
indices and values of the vector we want to store. Let `norm` be the l2 norm of the vector.
Then, the following code generates the correct tuples:

```scala
    val tuples = for ((index, value) <- indices zip values) yield {
      accum += pow(value, 2)
      (index, value, accum / norm)
    }
``` 

We get fast query access by doing a binary search on the `index` entry of the tuple. We
get fast sample access by 
1) generating a number _x_ between 0 and 1, and 
2) doing a binary search for _x_ on the `accum / norm` entry of the tuple.

### Matrix

The _m_ x _n_ matrix is stored by maintaining a `Row -> Vector` map for each row
in the matrix. This gives us `O(1)` access to any given row in our matrix.

## Requirements 

* Java 8
* [Maven 3.x](https://maven.apache.org/download.cgi) (or [SBT 1.x](https://www.scala-sbt.org/1.x/docs/index.html))

## Usage 
The simplest way to interact with the project is via **IntelliJ**. Usage via Maven:
```
mvn clean install
mvn exec:java -Dexec.mainClass=org.sabee.qrecc.Tang18
```

## License 

This project is licensed under the MIT license. 

## Acknowledgements 

Thanks to Ewin Tang and Patrick Rall for helpful discussions.

## Contact

Open an issue with any questions/comments.