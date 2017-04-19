#!/usr/bin/python
import os,sys,math
from Scanner import *

class CExpParser:
    """ 1<3 """
    def __init__(self, scanner, tableHead, dataItem):
        self.scanner = scanner
        self.res = True
        self.tableHead=tableHead
        self.dataItem = dataItem

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

        for i in range(0, len(para)):
            s = para[i]
            if len(s)<=0:
                self.res = False
                return False
            if s[0]=="'":
                para[i] = para[i][1:-1]
            elif s[0]=="+" or s[0]=="-" or s[0]=='.' or (s[0]>='0' and s[0]<='9'):
                para[i] = float(para[i])
            elif s[0]=="_" or (s[0] >= "a" and s[0]<="z") or (s[0]>="A" and s[0]<="Z"):
                para[i] = self.dataItem[self.tableHead[s]]

        if len(para)==1:
            if para[0]==0 or len(str(para[0]))==0:
                return False
            return True
        elif len(para)==3:
            if para[1]=="==" or para[1]=="=":
                if para[0] == para[2]:
                    return True
                return False
            elif para[1]==">":
                if para[0] > para[2]:
                    return True
                return False
            elif para[1]=="<":
                if para[0] < para[2]:
                    return True
                return False
            elif para[1]==">=":
                if para[0] >= para[2]:
                    return True
                return False
            elif para[1]=="<=":
                if para[0] <= para[2]:
                    return True
                return False
            elif para[1]=="!=":
                if para[0] != para[2]:
                    return True
                return False
        return False
 

class ExpParser(CExpParser):
    """ 1<3 AND 1<4"""
    def __init__(self, scanner, tableHead, dataItem):
        CExpParser.__init__(self, scanner, tableHead, dataItem)

    def parse(self):
        para=[]
        if self.scanner.token == "(":
            self.scanner.next()
            para.append(self.parse())
            self.scanner.next()
        else:
            para.append(self.cparse())

        if self.scanner.token.upper() == "AND" or self.scanner.token.upper() == "OR":
            para.append(self.scanner.token.upper())
            self.scanner.next()
            para.append(self.parse())

        if len(para)<=0:
            return True
        elif len(para)==1:
            return para[0]
        elif len(para)==3:
            if para[1]=="AND":
                return para[0] and para[2]
            elif para[1]=="OR":
                return para[0] or para[2]
        else:
            return False



    
        
                
import random
if __name__ == "__main__":
    oplist = [">=","<=", ">", "<", "==", "!="]
    def genStr(n):
        ans=""
        if n==0:
            return str(random.randint(1,1000)) + oplist[random.randint(0,5)] + " " + str(random.randint(1,1000));
        else:
            if random.randint(0,1)==0:
                ans = genStr(n-1) + " and " + genStr(n-1)
            else:
                ans = genStr(n-1) + " or  " + genStr(n-1)
        ans = "(" + ans + ")"
                
        return ans

    for i in range(1,10000):
        tests=genStr(5)
        print i
        p = ExpParser(Scanner(tests), {}, [])
        if p.parse()!=eval(tests):
            print tests

    print ExpParser(Scanner("221== 924 or  621== 395 and 400== 683 or  979!= 543"),{},[]).parse(), eval("221== 924 or  621== 395 and 400== 683 or  979!= 543")
    


