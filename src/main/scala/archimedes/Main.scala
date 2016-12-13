package archimedes

/**
  * Created by waseemh on 11/23/16.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val archimedes = new Archimedes;
    archimedes.measure("http://netflix.com")
    System.exit(0)
  }

}
