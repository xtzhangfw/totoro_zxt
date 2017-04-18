#!/usr/bin/python
import os,sys,math
from Scanner import *

class CExpParser:
    """ 1<3 """
    def __init__(self, scanner, tableHead, dataItem):
        self.scanner = scanner
        self.res=False

    def parse(self):
        val1,val2 = 0,0
        if self.scanner.token=="'":
            self.scanner.next()
            val1 = ""
            while self.scanner.token != "'":
                val1 = val1 + self.scanner.token
                self.scanner.next()
            self.scanner.next()
            

        
                
if __name__ == "__main__":
    


