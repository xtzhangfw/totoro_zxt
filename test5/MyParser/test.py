#!/usr/bin/python
import os,sys,math
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
	if random.randint(0,1)>10:
            ans = "(" + ans + ")"
                
        return ans

    fp=open("exp.txt","w")
    for i in range(1,10):
        tests=genStr(2)
        fp.write(tests + "\n")
	print eval(tests)
    fp.close()
        
    


