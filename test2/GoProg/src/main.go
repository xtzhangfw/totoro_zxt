package main

import (
	"Totoro"
	"fmt"
)

func groupByCol(rec *Totoro.Record, colIndex int) *Totoro.Record {
	for i := 0; i < rec.Len(); i++ {
		rec.Recs[i].GroupName = rec.Recs[i].Item[colIndex]
	}
	rec.RecSort()
	return rec
}

func filterByCol(rec *Totoro.Record, colIndex int, target string) *Totoro.Record {
	recNew := new(Totoro.Record)
	for i := 0; i < rec.Len(); i++ {
		val := rec.Recs[i].Item[colIndex]
		if val == target {
			recNew.RecAppend(rec.Recs[i])
		}
	}
	return recNew
}

func selectorByCol(rec *Totoro.Record, xCol, yCol int) *Totoro.Record {
	recNew := new(Totoro.Record)
	for i := 0; i < rec.Len(); i++ {
		item := Totoro.RecItem{GroupName: rec.Recs[i].GroupName, TableName: rec.Recs[i].TableName, Item: make([]string, 2)}
		item.Item[0] = rec.Recs[i].Item[xCol]
		item.Item[1] = rec.Recs[i].Item[yCol]
		recNew.RecAppend(item)
	}
	return recNew
}

func main() {
	rec := new(Totoro.Record)
	rec.RecGetDB("payment:payment@/totoro", "totoro_time_revenue_stas", "totoro_advertiser_revenue_stas")
	rec = groupByCol(rec, 2)
	rec = filterByCol(rec, 1, "168234")
	recNew := selectorByCol(rec, 3, 5)
	fmt.Println(recNew)
}
