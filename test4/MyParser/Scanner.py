#!/usr/bin/python
import os,sys,math

class Scanner:
    def __init__(self, sentence):
        self.sentence = sentence
        self.token = ""
        self.point = 0
        self.next()


    def isAb_(self, s):
        if (s>="a" and s<="z") or (s>="A" and s<="Z") or (s>="0" and s<="9") or s=="_" or s=="+" or s=="-" or s==".":
            return True
        return False

    def isBlank(self, s):
        if s==" " or s=="\t" or s=="\n":
            return True
        return False

    def next(self):
        self.token=""
        while self.point<len(self.sentence) and self.isBlank(self.sentence[self.point]):
            self.point += 1
        if self.point >= len(self.sentence):
            return
            
        bgn = self.point
        if self.point < len(self.sentence) and self.isAb_(self.sentence[self.point]):
            while self.point < len(self.sentence) and self.isAb_(self.sentence[self.point]):
                self.point += 1
            self.token = self.sentence[bgn:self.point]

        ###for >= <= > < == != =
        elif self.point < len(self.sentence) and (  
                    self.sentence[self.point] == ">" or 
                    self.sentence[self.point] == "<" or 
                    self.sentence[self.point] == "=" or 
                    self.sentence[self.point] == "!" ):

            if self.point+1 < len(self.sentence) and self.sentence[self.point+1] == "=":
                self.token = self.sentence[bgn:self.point+2]
                self.point += 2
            else:
                self.token = self.sentence[bgn:bgn+1]
                self.point += 1

        ##for 'string'
        elif self.point < len(self.sentence) and self.sentence[self.point] == "'":
            self.point += 1
            while self.point < len(self.sentence) and self.sentence[self.point]!="'":
                self.point += 1
            self.token = self.sentence[bgn:self.point+1]
            self.point += 1
            return

        else:
            self.token = self.sentence[self.point]
            self.point += 1


if __name__ == "__main__":
    s = Scanner("main( a>1,c!=0.89, ' b c  d', '-8' ,+0.8e100), e(a),  a,  ' b c d'")
    while s.token!="":
        print s.token
        s.next()
    


