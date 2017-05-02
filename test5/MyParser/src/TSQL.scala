
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

  def isColName(s:String): Boolean ={
    if(s.length<=0) false
    else{
      if((s(0)>='a' && s(0)<='z') || (s(0)>='A' && s(0)<='Z')) true
      else false
    }
  }

  def isList(v:Any):Boolean = {
    v match{
      case _:List[Any]=>true
      case _=>false
    }
  }

  def defaultFlatten(fun: List[Any]):List[Any]={
    val funName = fun.head.toString.toUpperCase()
    val par = fun.tail
    val newPar = par.map(f=>
      if(isList(f)){defaultFlatten(f.asInstanceOf[List[Any]])}
      else{f}
    )
    val nNewPar=newPar.flatMap(f=>
      if(isList(f) && f.asInstanceOf[List[Any]].head.toString=="DEFAULT"){
        f.asInstanceOf[List[Any]].tail
      }
      else{
        List(f)
      }
    )
    funName :: nNewPar
  }

  def calMapFun(fun: List[Any]):List[Any]={
    val funName = fun.head.toString.toUpperCase()
    val par = fun.tail
    val newPar = par.map(f=>
      if(isList(f)){calMapFun(f.asInstanceOf[List[Any]])}
      else{ f }
    )

    def canCal(parL:List[Any]):Boolean={
      if(parL.length<=0) return true
      if(isList(parL.head)){
        val par = parL.head.asInstanceOf[List[Any]]
        if(par.head.toString!="DEFAULT"){ return false }
        else{
          val flag = canCal(par)
          if(!flag) return false
        }
      }
      canCal(parL.tail)
    }

    if(!canCal(newPar)){ return funName :: newPar }
    else{
      if(funName=="ABS"){
        val nNewPar = newPar.map(f=>
          if(isList(f)){ abs(f.asInstanceOf[List[Any]].tail.head.toString.toFloat).toString}
          else{ abs(f.toString.toFloat).toString}
        )
        return "DEFAULT" :: nNewPar
      }
      else{
        return funName :: newPar
      }
    }
  }

  def reducefun(lA:List[Any], lB:List[Any]):List[Any]={
    
    Nil
  }

  def mapfun(s:String, sql: String,
             tableInfo:(String,mutable.Map[String, Int],mutable.Map[String,String])):(String, List[Any])={

    val scanner = new Scanner(sql, 0).next()._2
    val pr = new Parser
    val ans1 = pr.funListParse(scanner, Nil)
    val selList = "DEFAULT" :: ans1._1

    val ans2 = ans1._2.next()._2.next()._2.next()

    val conScanner =  if(ans2._1.toUpperCase() == "WHERE"){ ans2._2 }else{ new Scanner("1",0)}
    val scGroup = if(ans2._1.toUpperCase()=="WHERE"){ pr.expParse(ans2._2,"")._2 }else{ ans1._2 }
    val groupScanner=if(scGroup.next()._1.toUpperCase() == "GROUP"){scGroup.next()._2}else{ new Scanner("GROUPALL(*)",0)}
    val groupList = "DEFAULT" :: pr.funListParse(groupScanner, Nil)._1

    val item = s.split(Cst.SPACE)

    val flag = pr.expCal(conScanner, tableInfo, item, true, "AND")._1

    def repFun(fun: List[Any]):List[Any]={//replace the identifier with value
      val funName = fun.head.toString.toUpperCase()
      val par = fun.tail
      val newPar = par.map(f=>
        if(isList(f)){ repFun(f.asInstanceOf[List[Any]])}
        else{
          if(isColName(f.toString)){ item(tableInfo._2(f.toString)) }
          else if(f.toString=="*"){ "DEFAULT" :: item.toList }
          else{ f }
        }
      )
      funName :: newPar
    }

    if(!flag){ ("",Nil) }
    else{
      val gl = defaultFlatten(calMapFun(repFun(groupList)))
      val glStr = gl.tail.toString()
      val sl = defaultFlatten(calMapFun(repFun(selList)))
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
    //val rdd2 = rdd1.reduceByKey(Funs.reducefun).collect()
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
    query("select network_id,price_model, abs(sum(abs(network_id,2000), abs(network_id), abs(2000,2000))) from d_placement where network_id=168282 group network_id")
  }


}