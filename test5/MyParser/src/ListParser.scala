package MyParser

import java.util

import MyScanner.Scanner

import scala.collection.mutable
/*
class FunItem(val FunName:String, val ParaList: List[String]){
  override def toString: String = {
    super.toString
    val para = ParaList.mkString(",")
    val ans = FunName + "(" + para + ")"
    ans
  }
}
*/

class Parser {

  def cexpCal(scanner: Scanner,
              tableInfo:(String, mutable.Map[String,Int],mutable.Map[String,String]),
              dataItem: Array[String]): (Boolean, Scanner) ={

    val tmp = scanner.next()
    val tmp1 = tmp._2.next()
    if(tmp._1 == "("){
      val tmp2 = expCal(tmp._2, tableInfo, dataItem, true, "AND")
      val tmp3 = tmp2._2.next()
      (tmp2._1, tmp3._2)
    }
    else if(tmp1._1 == ">" || tmp1._1 == "<" || tmp1._1 == "=" ||
      tmp1._1 == ">=" || tmp1._1 == "<=" ||
      tmp1._1 == "!=" || tmp1._1 == "==" ) {
      val tmp2 = tmp1._2.next()

      val vtype = if(tmp._1(0)=='\'' || tmp2._1(0)=='\''){ "STRING"}
      else if((tmp._1(0)>='a' && tmp._1(0)<='z') || (tmp._1(0)>='A' && tmp._1(0)<='Z') || tmp._1(0)=='_'){
        tableInfo._3(tmp._1)
      }
      else if((tmp2._1(0)>='a' && tmp2._1(0)<='z') || (tmp2._1(0)>='A' && tmp2._1(0)<='Z') || tmp2._1(0)=='_'){
        tableInfo._3(tmp2._1)
      }
      else{
        "FLOAT"
      }
      val p1 = tmp._1
      val p2 = tmp2._1
      val para1 = if(p1.length>0 && ((p1(0)>='a' && p1(0)<='z') || (p1(0)>='A' && p1(0)<='Z'))){dataItem(tableInfo._2(p1))}else{p1}
      val para2 = if(p2.length>0 && ((p2(0)>='a' && p2(0)<='z') || (p2(0)>='A' && p2(0)<='Z'))){dataItem(tableInfo._2(p2))}else{p2}

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
        if(tableInfo._3(tmp._1) == "INT"){ dataItem(tableInfo._2(tmp._1).toInt).toInt}
        else if(tableInfo._3(tmp._1) == "FLOAT"){ dataItem(tableInfo._2(tmp._1).toInt).toFloat}
        else if(tableInfo._3(tmp._1) == "STRING"){ dataItem(tableInfo._2(tmp._1).toInt).toString}
        else{ dataItem(tableInfo._2(tmp._1).toInt).toString}
      }

      val ans = if(para1==0 || para1=="")false else true
      (ans, tmp._2)
    }
  }

  def expCal(scanner: Scanner,
             tableInfo:(String, mutable.Map[String,Int],mutable.Map[String,String]),
             dataItem:Array[String],
             sta:Boolean, op:String): (Boolean, Scanner) = {

    val tmp = exp1Cal(scanner,tableInfo, dataItem, true, "AND")
    val ans:Boolean = if(op=="AND"){ tmp._1 && sta }else{ tmp._1 || sta }
    val tmp1 = tmp._2.next()
    if(tmp1._1.toUpperCase() == "OR"){
      expCal(tmp1._2, tableInfo, dataItem, ans, tmp1._1.toUpperCase)
    }
    else{
      (ans, tmp._2)
    }
  }

  def exp1Cal(scanner: Scanner,
              tableInfo:(String, mutable.Map[String,Int],mutable.Map[String,String]),
              dataItem:Array[String],
              sta:Boolean, op:String): (Boolean, Scanner) = {
    val tmp = cexpCal(scanner,tableInfo, dataItem)
    val ans:Boolean = if(op=="AND"){ tmp._1 && sta }else{ tmp._1 || sta }
    val tmp1 = tmp._2.next()
    if(tmp1._1.toUpperCase() == "AND"){
      exp1Cal(tmp1._2, tableInfo, dataItem, ans, tmp1._1.toUpperCase)
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

  def funListParse(scanner: Scanner, ans:List[List[String]]): (List[List[String]], Scanner) = {
    val tmp = scanner.next()
    if(tmp._1 == ","){
      funListParse(tmp._2, ans)
    }
    else{
      val tmp1 = tmp._2.next()
      if(tmp1._1 == "("){
        val para = listParse(tmp1._2, Nil)
        val item = tmp._1 :: para._1
        val tmp2 = para._2.next()
        if(tmp2._2.next()._1==","){ funListParse(tmp2._2, item :: ans) }
        else{ ((item :: ans).reverse, tmp2._2)}
      }
      else if(tmp1._1 == ","){
        val item = "DEFAULT" :: List(tmp._1)
        funListParse(tmp1._2, item :: ans)
      }
      else{
        val item = "DEFAULT" :: List(tmp._1)
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
      val ansexpcal = pr.expCal(new Scanner(calstr, 0), ("", mutable.Map(), mutable.Map()), Array(), true, "AND")
      if(ansexpcal._1){ outf.write("True\n")}
      else{ outf.write("False\n")}
      //println(line)
    }
    outf.close()
  }
}