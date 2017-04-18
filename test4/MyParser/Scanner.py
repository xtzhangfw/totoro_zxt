#!/usr/bin/python
import os,sys,math

class Scanner:
    def __init__(self, sentence):
        self.sentence = sentence
        self.token = ""
        self.point = 0
        self.next()
    
    def isAb_(self, s):
        if (s>="a" and s<="z") or (s>="A" and s<="Z") or (s>="0" and s<="9") or s=="_":
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
        if self.isAb_(self.sentence[self.point]):
            while self.point < len(self.sentence) and self.isAb_(self.sentence[self.point]):
                self.point += 1
            self.token = self.sentence[bgn:self.point]
        else:
            self.token = self.sentence[self.point]
            self.point += 1


if __name__ == "__main__":
    s = Scanner("main( a,b+1 )")
    while s.token!="":
        print s.token
        s.next()
    


