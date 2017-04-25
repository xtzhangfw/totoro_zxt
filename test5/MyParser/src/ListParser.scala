import java.util

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

  def cexpCal(scanner: Scanner, tableHead: Map[String, Array[String]], dataItem: Array[String]): (Boolean, Scanner) ={
    val tmp = scanner.next()
    val tmp1 = tmp._2.next()
    if(tmp._1 == "("){
      val tmp2 = expCal(tmp._2, tableHead, dataItem, true, "AND")
      val tmp3 = tmp2._2.next()
      (tmp2._1, tmp3._2)
    }
    else if(tmp1._1 == ">" || tmp1._1 == "<" || tmp1._1 == "=" ||
      tmp1._1 == ">=" || tmp1._1 == "<=" ||
      tmp1._1 == "!=" || tmp1._1 == "==" ) {
      val tmp2 = tmp1._2.next()

      val vtype = if(tmp._1(0)=='\'' || tmp2._1(0)=='\''){ "STRING"}
      else if((tmp._1(0)>='a' && tmp._1(0)<='z') || (tmp._1(0)>='A' && tmp._1(0)<='Z') || tmp._1(0)=='_'){
        tableHead(tmp._1)(0)
      }
      else if((tmp2._1(0)>='a' && tmp2._1(0)<='z') || (tmp2._1(0)>='A' && tmp2._1(0)<='Z') || tmp2._1(0)=='_'){
        tableHead(tmp2._1)(0)
      }
      else{
        "FLOAT"
      }
      val para1 = tmp._1
      val para2 = tmp2._1
      val ans = if(vtype=="INT") {
        if (tmp1._1 == ">") {
          if (para1.toInt > para2.toInt) true else false
        }
        else if (tmp1._1 == "<") {
          if (para1.toInt < para2.toInt) true else false
        }
        else if (tmp1._1 == "=" || tmp1._1 == "==") {
          if (para1.toInt == para2.toInt) true else false
        }
        else if (tmp1._1 == ">=") {
          if (para1.toInt >= para2.toInt) true else false
        }
        else if (tmp1._1 == "<=") {
          if (para1.toInt <= para2.toInt) true else false
        }
        else if (tmp1._1 == "!=") {
          if (para1.toInt != para2.toInt) true else false
        }
        else {
          false
        }
      }else if(vtype=="FLOAT"){
        if (tmp1._1 == ">") {
          if (para1.toFloat > para2.toFloat) true else false
        }
        else if (tmp1._1 == "<") {
          if (para1.toFloat < para2.toFloat) true else false
        }
        else if (tmp1._1 == "=" || tmp1._1 == "==") {
          if (para1.toFloat == para2.toFloat) true else false
        }
        else if (tmp1._1 == ">=") {
          if (para1.toFloat >= para2.toFloat) true else false
        }
        else if (tmp1._1 == "<=") {
          if (para1.toFloat <= para2.toFloat) true else false
        }
        else if (tmp1._1 == "!=") {
          if (para1.toFloat != para2.toFloat) true else false
        }
        else {
          false
        }
      }else{
        if (tmp1._1 == ">") {
          if (para1.toString > para2.toString) true else false
        }
        else if (tmp1._1 == "<") {
          if (para1.toString < para2.toString) true else false
        }
        else if (tmp1._1 == "=" || tmp1._1 == "==") {
          if (para1.toString == para2.toString) true else false
        }
        else if (tmp1._1 == ">=") {
          if (para1.toString >= para2.toString) true else false
        }
        else if (tmp1._1 == "<=") {
          if (para1.toString <= para2.toString) true else false
        }
        else if (tmp1._1 == "!=") {
          if (para1.toString != para2.toString) true else false
        }
        else {
          false
        }
      }

      (ans, tmp2._2)

    }
    else{
      val para1 = if(tmp._1(0) == '\''){
        tmp._1.substring(1, tmp._1.length - 1)
      }else if(tmp._1(0)=='+' || tmp._1(0)=='-' || tmp._1(0)=='.' || (tmp._1(0)>='0' && tmp._1(0)<='9')){
        tmp._1.toFloat
      }else{
        if(tableHead(tmp._1)(0) == "INT"){ dataItem(tableHead(tmp._1)(1).toInt).toInt}
        else if(tableHead(tmp._1)(0) == "FLOAT"){ dataItem(tableHead(tmp._1)(1).toInt).toFloat}
        else if(tableHead(tmp._1)(0) == "STRING"){ dataItem(tableHead(tmp._1)(1).toInt).toString}
        else{ dataItem(tableHead(tmp._1)(1).toInt).toString}
      }

      val ans = if(para1==0 || para1=="")false else true
      (ans, tmp._2)
    }
  }

  def expCal(scanner: Scanner, tableHead: Map[String,Array[String]], dataItem:Array[String], sta:Boolean, op:String): (Boolean, Scanner) = {
    val tmp = cexpCal(scanner,tableHead, dataItem)
    val ans:Boolean = if(op=="AND"){ tmp._1 && sta }else{ tmp._1 || sta }
    val tmp1 = tmp._2.next()
    if(tmp1._1.toUpperCase() == "AND" || tmp1._1.toUpperCase() == "OR"){
      expCal(tmp1._2, tableHead, dataItem, ans, tmp1._1.toUpperCase)
    }
    else{
      (ans, tmp._2)
    }
  }



  def cexpParse(scanner:Scanner):(String, Scanner) = {
    val tmp = scanner.next()
    val tmp1 = tmp._2.next()
    if(tmp._1 == "("){
      val tmp2 = expParse(tmp._2,"")
      val tmp3 = tmp2._2.next()
      ("( " + tmp2._1 + " )", tmp3._2)
    }
    else if(tmp1._1 == ">" || tmp1._1 == "<" || tmp1._1 == "=" ||
       tmp1._1 == ">=" || tmp1._1 == "<=" ||
       tmp1._1 == "!=" || tmp1._1 == "==" ){
      val tmp2 = tmp1._2.next()
      (tmp._1 + " " + tmp1._1 + " " + tmp2._1, tmp2._2)
    }
    else{
      (tmp._1, tmp._2)
    }
  }

  def expParse(scanner: Scanner, cur:String):(String, Scanner) = {
    val tmp = cexpParse(scanner)
    val tmp1 = tmp._2.next()
    if(tmp1._1.toUpperCase() == "AND" || tmp1._1.toUpperCase() == "OR"){
      expParse(tmp1._2, cur + " " + tmp._1 + " " + tmp1._1)
    }
    else{
      (cur + " " + tmp._1, tmp._2)
    }
  }

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

import scala.util.Random

object TestListParser {
  def main(args: Array[String]): Unit = {
    val pr = new Parser
    val ans = pr.listParse(new Scanner("  hello   , world, 12,'234', a b c d", 0), Nil)
    val ansf = pr.funListParse(new Scanner(" AVE(  a,  'b  ahe' , -0.2e2.0, +9e8  ),  MIN(a), b , c,,  ", 0), Nil)
    val ansexp = pr.expParse(new Scanner("  1<= 2 and (2>'3' or '3'!='4') and ( a>'3') UNION hello", 0),"")

    import scala.io.Source
    import java.io._
    val sc = Source.fromFile("exp.txt")
    val outf = new PrintWriter(new File("scala_res.txt"))
    for(line <- sc.getLines()) {
      val calstr = line
      val ansexpcal = pr.expCal(new Scanner(calstr, 0), Map(), Array(), true, "AND")
      if(ansexpcal._1){ outf.write("True\n")}
      else{ outf.write("False\n")}
    }
    outf.close()
  }
}