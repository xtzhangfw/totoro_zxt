#!/usr/bin/python
import os,sys,math
from Scanner import *

class ListParser:
    def __init__(self, scanner):
        """col1, col2"""
        self.scanner = scanner
        self.res=[]

    def parse(self):
        while True:
            if self.scanner.token != ",":
                self.res.append(self.scanner.token)
                self.scanner.next()
                if self.scanner.token != ",":
                    return
            else:
                self.scanner.next()

class FunParser:
    """ MAX(arg,arg2)"""
    def __init__(self, scanner):
        self.scanner = scanner
        self.res={"FUNNAME":"", "PARAS":[]}

    def parse(self):
        self.res["FUNNAME"]=self.scanner.token
        self.scanner.next()
        if self.scanner.token!="(":
            return
        self.scanner.next()
        lp = ListParser(self.scanner); lp.parse()
        self.res["PARAS"] = lp.res
        self.scanner.next()

class FunListParser:
    """ MAX(arg,arg2),col1,col2"""
    def __init__(self, scanner):
        self.scanner = scanner
        self.res=[]

    def parse(self):
         while True:
            if self.scanner.token != ",":
                name = self.scanner.token
                self.scanner.next()
                if self.scanner.token == "(":
                    self.scanner.next()
                    lp = ListParser(self.scanner); lp.parse()
                    self.res.append({"FUNNAME":name, "PARAS":lp.res})

                    self.scanner.next()
                    if self.scanner.token != ",":
                        return
                    else:
                        self.scanner.next()

                elif self.scanner.token == ",":
                    self.res.append({"FUNNAME":"DEFAULT", "PARAS":[name]})
                    self.scanner.next()

                else:
                    self.res.append({"FUNNAME":"DEFAULT", "PARAS":[name]})
                    self.scanner.next()
                    return
            else:
                self.scanner.next()

                
if __name__ == "__main__":
    p = FunListParser(Scanner(" AVE(  a,  b  ),  MIN(a), b , c,,"))
    p.parse()
    print p.res
    


