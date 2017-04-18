#!/usr/bin/python
from pyspark import SparkContext, SparkConf
import sys,os,math
import json

tableName=sys.argv[1]
xcol=int(sys.argv[2])
ycol=int(sys.argv[3])
gcol=sys.argv[4:]
for i in range(0, len(gcol)):
    gcol[i] = int(gcol[i])

conf = SparkConf()
conf.setAppName("MyWordCount")
conf.setMaster("spark://ubuntu:7077")
sc = SparkContext(conf=conf)

rdd = sc.textFile("hdfs://localhost:9000/Tables/" + tableName)

def mysplit(line):
    items = line.split("#")
    glist=[]
    for i in range(0, len(gcol)):
        glist.append(items[gcol[i]])

    return (tuple(glist),(items[xcol],items[ycol]))

res = rdd.map(mysplit).groupByKey().map(lambda x: (x[0],list(x[1]))).collect()

res = res[:50]

jsonData=[]
for gdata in res:
    item={}
    item["Group"]=gdata[0]
    item["XY"] = gdata[1]
    jsonData.append(item)

print json.dumps(jsonData)
sc.stop()
