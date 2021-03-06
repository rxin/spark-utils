package main.scala

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object GraphXApp {
  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Simple GraphX App")
    val users: RDD[(VertexId, (String, String))] =
      sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
                           (5L, ("franklin", "prof")), (2L, ("istoica", "prof")),
                           (4L, ("peter", "student"))))
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 7L, "collab"),    Edge(5L, 3L, "advisor"),
                           Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi"),
                           Edge(4L, 0L, "student"),   Edge(5L, 0L, "colleague")))
    val defaultUser = ("John Doe", "Missing")
    val graph = Graph(users, relationships, defaultUser)
    // Notice that there is a user 0 (for which we have no information) connected to users
    // 4 (peter) and 5 (franklin).
    val triplets = graph.triplets.map(e => (e.srcAttr._1, e.dstAttr._1)).collect
    if (!triplets.exists(_ == ("peter", "John Doe"))) {
      println("Failed to run GraphX")
      System.exit(-1)
    }
    println("Test succeeded")
  }
}
