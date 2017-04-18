#!/usr/bin/python
import os,sys,math
from Scanner,ListParser import *

class SelectParser:
    """parse select sentence"""
    def __init(self, scanner):
        self.scanner = scanner
        self.colList = []
        self.

    def parse():
        if self.scanner.token.upper() != "SELECT":
            return 
        scanner.next()
        
        


                
if __name__ == "__main__":
    p = FunListParser(Scanner(" AVE(  a,  b  ),  MIN(a), b , c,,"))
    p.parse()
    print p.res
    


