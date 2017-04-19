#!/usr/bin/python
import os,sys,math
from Scanner import *

class ExpParser:
    def __init__(self, scanner):
        self.res=""
        self.scanner = scanner

    def cparse(self):
        para = []
        str1 = self.scanner.token
        para.append(str1)

        self.scanner.next()
        if  self.scanner.token == ">" or \
            self.scanner.token == "<" or \
            self.scanner.token == "=" or \
            self.scanner.token == "==" or \
            self.scanner.token == ">=" or \
            self.scanner.token == "<=" or \
            self.scanner.token == "!=":
                
            para.append(self.scanner.token)
            self.scanner.next()
            para.append(self.scanner.token)
            self.scanner.next()
        return " ".join(para)

    def parse(self):
        para=[]
        if self.scanner.token == "(":
            para.append(self.scanner.token)
            self.scanner.next()
            para.append(self.parse())
            para.append(self.scanner.token)
            self.scanner.next()
        else:
            para.append(self.cparse())

        if self.scanner.token.upper() == "AND" or self.scanner.token.upper() == "OR":
            para.append(self.scanner.token.upper())
            self.scanner.next()
            para.append(self.parse())
        return " ".join(para)

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
                    return self.res
            else:
                self.scanner.next()
        return self.res

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
                        return self.res
                    else:
                        self.scanner.next()

                elif self.scanner.token == ",":
                    self.res.append({"FUNNAME":"DEFAULT", "PARAS":[name]})
                    self.scanner.next()

                else:
                    self.res.append({"FUNNAME":"DEFAULT", "PARAS":[name]})
                    self.scanner.next()
                    return self.res
            else:
                self.scanner.next()
        return self.res

                
if __name__ == "__main__":
    print FunListParser(Scanner(" AVE(  a,  'b  ahe' , -0.2e2.0, +9e8  ),  MIN(a), b , c,,")).parse()
    print ExpParser(Scanner("  1<= 2 and (2>'3' or '3'!='4') and ( a>'3') UNION hello")).parse()
    

