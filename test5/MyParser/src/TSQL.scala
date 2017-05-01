
import java.io.File

import MyScanner.Scanner
import MyParser.Parser
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.hadoop.fs._

import scala.collection.mutable
import scala.math._

object Cst{
  val SPACE=Character.toString(1.toChar)
}

object Funs{

  def mapfun(s:String, sql: String,
             tableInfo:(String,mutable.Map[String, Int],mutable.Map[String,String])):(String, List[List[String]])={

    val scanner = new Scanner(sql, 0).next()._2
    val pr = new Parser
    val ans1 = pr.funListParse(scanner, Nil)
    val selList = ans1._1

    val ans2 = ans1._2.next()._2.next()._2.next()

    val conScanner =  if(ans2._1.toUpperCase() == "WHERE"){ ans2._2 }else{ new Scanner("1",0)}
    val scGroup = if(ans2._1.toUpperCase()=="WHERE"){ pr.expParse(ans2._2,"")._2 }else{ ans1._2 }
    val groupScanner=if(scGroup.next()._1.toUpperCase() == "GROUP"){scGroup.next()._2}else{ new Scanner("GROUPALL(*)",0)}
    val groupList = pr.funListParse(groupScanner, Nil)._1

    val item = s.split(Cst.SPACE)

    val flag = pr.expCal(conScanner, tableInfo, item, true, "AND")._1

    def calFunList(fl: List[List[String]], cur: List[List[String]]):List[List[String]]={
      if(fl.length<=0) cur.reverse
      else {
        val para = fl.head.tail.map(
          s => if ((s(0) >= 'a' && s(0) <= 'z') || (s(0) >= 'A' && s(0) <= 'Z')) {
            item(tableInfo._2(s))
          } else {
            s
          })
        val funName = fl.head.head
        if (funName == "ABS") {
          val fitem = "DEFAULT" :: List(abs(para.head.toFloat).toString)
          calFunList(fl.tail, fitem :: cur)
        } else {
          val fitem = funName :: para
          calFunList(fl.tail, fitem :: cur)
        }
      }
    }


    if(flag==false){ ("",Nil) }
    else{
      val gl = calFunList(groupList, Nil)
      val glStr = gl.map(l=>l.tail.head).mkString(" ")
      val sl = calFunList(selList, Nil)
      (glStr, sl)
    }

  }
}

object TSQL {

  val tablePath="hdfs://localhost:9000/Tables"
  val tableList = getTableList()
  val tableInfo = mutable.Map[String,(String, mutable.Map[String,Int], mutable.Map[String,String])]()


  for( tn <- tableList){
    tableInfo += (tn -> getTableInfo(tn))
  }


  def getTableList():Array[String]={
    val conf = new SparkConf().setAppName("GetTableList")
    val sc = new SparkContext(conf)
    val fList = FileSystem.get( sc.hadoopConfiguration ).globStatus( new Path(tablePath + "/" + "*.info"))
    val ans=new Array[String](fList.length)
    for(i <- 0 until fList.length){
      ans(i) = fList(i).getPath.getName
      val ln = ans(i).length
      ans(i) = ans(i).substring(0, ln-5)
    }
    sc.stop()
    ans
  }

  def getTableInfo(tname: String):(String, mutable.Map[String,Int], mutable.Map[String,String])={
    val conf = new SparkConf().setAppName("GetTableInfo " + tname)
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(tablePath + "/" + tname + ".info")
    val lines = rdd.map(l=>l.split(Character.toString(1.toChar))).collect()
    val des = lines(0).mkString(" ")
    val colName = mutable.Map[String, Int]()
    val colType = mutable.Map[String, String]()
    for(i <- 0 until lines(1).length){
      colName += (lines(1)(i)->i)
      colType += (lines(1)(i)->lines(2)(i))
    }
    sc.stop()
    (des, colName, colType)
  }

  def cmd_SHOW(sc: Scanner): Array[Array[String]] = {
    val sc1 = sc.next()
    val pa = sc1._1.toUpperCase()
    if(pa=="TABLES"){
      Array(tableList)
    }else{
      Array(Array())
    }
  }

  def cmd_SELECT(sql: String): Array[Array[String]]= {

    val scanner = new Scanner(sql, 0).next()._2
    val pr = new Parser
    val ans1 = pr.funListParse(scanner, Nil)
    val tname = ans1._2.next()._2.next()._1

    val tInfo = ("hello", tableInfo(tname)._2, tableInfo(tname)._3)
    val conf = new SparkConf().setAppName("select")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(tablePath + "/" + tname)

    val rdd1 = rdd.map(Funs.mapfun(_,sql,tInfo)).filter(a=>a._1.length>0).collect()
    for(r<-rdd1){println(r)}

    Array(Array())
  }

  def query(sql: String): Array[Array[String]] ={
    val scanner = new Scanner(sql, 0)
    val ans = scanner.next()
    val cmd = ans._1.toUpperCase()
    if(cmd == "SELECT"){
      cmd_SELECT(sql)
    }else if(cmd == "SHOW"){
      //cmd_SHOW(ans._2)
      Array(Array())
    }else{
      Array(Array())
    }
  }


  def main(args: Array[String]) {
    val ans = query("show tables")
    query("select network_id from d_placement where network_id=168282 group network_id")
    //cmd_SELECT(new Scanner("id,sum( age ,2 ,4),name where id>10 and name<10 group id,age,day(name)", 0))
  }


}