#!/usr/bin/python
import os,sys,math
from Scanner import *

class CExpParser:
    """ 1<3 """
    def __init__(self, scanner, tableHead, dataItem):
        self.scanner = scanner
        self.para=[]
        self.res = True
        self.tableHead=tableHead
        self.dataItem = dataItem

    def parse(self):
        str1 = self.scanner.token
        sel.para.append(str1)

        self.scanner.next()
        if  self.scanner.token == ">" or \
            self.scanner.token == "<" or \
            self.scanner.token == "=" or \
            self.scanner.token == "==" or \
            self.scanner.token == ">=" or \
            self.scanner.token == "<=" or \
            self.scanner.token == "!=":
                
            self.para.append(self.scanner.token)
            self.scanner.next()
            self.append(self.scanner.token)
            self.para.scanner.next()

        for i in range(0, len(self.para)):
            s = self.res[i]
            if len(s)<=0:
                self.res = False
                return False
            if s[0]=="'":
                self.res[i] = self.res[i][1:-1]
            elif s[0]=="+" or s[0]=="-" or s[0]=='.' or (s[0]>='0' and s[0]<='9'):
                self.res[i] = float(self.res[i])
            else s[0]=="_" or (s[0] >= "a" and s[0]<="z") or (s[0]>="A" and s[0]<="Z"):
                self.res[i] = self.dataItem[self.tableHead[s]]

        if len(self.para)==1:
            if self.para[0]==0 or len(str(self.para[0]))==0:
                return False
            return True
        elif len(self.para)==3:
            if self.para[1]=="==" or self.para[1]=="=":
                if self.para[0] == self.para[2]:
                    return True
                return False
            elif self.para[1]==">":
                if self.para[0] > self.para[2]:
                    return True
                return False
            elif self.para[1]=="<":
                if self.para[0] < self.para[2]:
                    return True
                return False
             elif self.para[1]==">=":
                if self.para[0] >= self.para[2]:
                    return True
                return False
             elif self.para[1]=="<=":
                if self.para[0] <= self.para[2]:
                    return True
                return False
             elif self.para[1]=="!=":
                if self.para[0] != self.para[2]:
                    return True
                return False
        return False
 
        
                
if __name__ == "__main__":
    


