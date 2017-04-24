import MyScanner.Scanner

class FunItem(val FunName:String, val ParaList: List[String]){
  override def toString: String = {
    super.toString
    val para = ParaList.mkString(",")
    val ans = FunName + "(" + para + ")"
    ans
  }
}

class Parser {

  def listParse(scanner: Scanner, ans: List[String]): (List[String], Scanner) = {
    val tmp = scanner.next()
    if (tmp._1 == ",") {
      val tmp1 = tmp._2.next()
      listParse(tmp1._2, tmp1._1 :: ans)
    }
    else if (tmp._1 != "," && ans.length == 0) {
      listParse(tmp._2, tmp._1 :: ans)
    }
    else {
      (ans.reverse, scanner)
    }
  }

  def funListParse(scanner: Scanner, ans:List[FunItem]): (List[FunItem], Scanner) = {
    val tmp = scanner.next()
    if(tmp._1 == ","){
      funListParse(tmp._2, ans)
    }
    else{
      val tmp1 = tmp._2.next()
      if(tmp1._1 == "("){
        val para = listParse(tmp1._2, Nil)
        val item = new FunItem(tmp._1, para._1)
        val tmp2 = para._2.next()
        funListParse(tmp2._2, item :: ans)
      }
      else if(tmp1._1 == ","){
        val item = new FunItem("DEFAULT", List(tmp._1))
        funListParse(tmp1._2, item :: ans)
      }
      else{
        val item = new FunItem("DEFAULT", List(tmp._1))
        ((item :: ans).reverse, tmp._2)
      }

    }
  }
}

object TestListParser {
  def main(args: Array[String]): Unit = {
    val pr = new Parser
    val ans = pr.listParse(new Scanner("  hello   , world, 12,'234', a b c d", 0), Nil)
    val ansf = pr.funListParse(new Scanner(" AVE(  a,  'b  ahe' , -0.2e2.0, +9e8  ),  MIN(a), b , c,,  ", 0), Nil)
    println(ansf._1.length)
    for(a <- ansf._1){
      println(a)
    }
  }
}