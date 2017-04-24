package MyScanner

class Scanner(val s:String, val point:Int){
  val ls = s.length
  def next(): (String, Scanner)={
    def skipSpace(cur:Int):Int = {
      if(cur >= ls) { ls }
      else{
        if(s(cur)==' ' || s(cur)=='\t' || s(cur)=='\n'){ skipSpace(cur+1)}
        else{ cur }
      }
    }

    def cmpToken(cur:Int, bgn:Int):(Int,String) = {
      if(cur >= ls){ (ls,"") }
      else{
        if(cur+1<ls && s(cur+1)=='='){ (cur+2, s.substring(cur, cur+2)) }
        else{ (cur+1,s.substring(cur, cur+1)) }
      }
    }

    def strToken(cur:Int, bgn:Int):(Int,String) = {
      if (cur>=ls){ (ls, s.substring(bgn, cur))}
      else if(cur == bgn){ strToken(cur+1, bgn) }
      else{
        if(s(cur)!='\''){ strToken(cur+1, bgn) }
        else{
          (cur+1, s.substring(bgn, cur+1))
        }
      }
    }

    def paraToken(cur:Int, bgn:Int): (Int, String) ={
      if(cur>=ls){ (ls, s.substring(bgn, cur))}
      else if(s(cur)=='.' ||
        (s(cur)>='a' && s(cur)<='z') ||
        (s(cur)>='A' && s(cur)<='Z') ||
        (s(cur)>='0' && s(cur)<='9') ||
        (s(cur)=='+') ||
        (s(cur)=='-') ||
        (s(cur)=='_')) { paraToken(cur+1, bgn) }
      else{
        (cur, s.substring(bgn, cur))
      }
    }

    def oneToken(cur:Int, bgn:Int):(Int, String) = {
      (cur+1, s.substring(cur, cur+1))
    }

    val cur = skipSpace(point)
    if(cur>=ls) ("", new Scanner(s, ls))
    else{
      if(s(cur)=='.' ||
        (s(cur)>='a' && s(cur)<='z') ||
        (s(cur)>='A' && s(cur)<='Z') ||
        (s(cur)>='0' && s(cur)<='9') ||
        (s(cur)=='+') ||
        (s(cur)=='-') ||
        (s(cur)=='_')) {

        val ans = paraToken(cur, cur)
        (ans._2, new Scanner(s, ans._1))
      }
      else if(s(cur)=='>' ||
              s(cur)=='<' ||
              s(cur)=='!' ||
              s(cur)=='='){
        val ans = cmpToken(cur,cur)
        (ans._2, new Scanner(s, ans._1))

      }
      else if(s(cur)=='\''){
        val ans = strToken(cur, cur)
        (ans._2, new Scanner(s, ans._1))
      }
      else{
        val ans = oneToken(cur, cur)
        (ans._2, new Scanner(s, ans._1))
      }
    }
  }
}

object TestScanner{
  def main(args: Array[String]): Unit = {
    val sc = new Scanner("  int a = 10; for(i=0;i<=10; i++){ print'hehe' } 'hehe'", 0)

    def test(scanner: Scanner): Unit = {
      val ans = scanner.next()

      if (ans._1 == "") { println("END") ; 0}
      else{
        println(ans._1)
        test(ans._2)
      }
    }

    test(sc)
  }


}

