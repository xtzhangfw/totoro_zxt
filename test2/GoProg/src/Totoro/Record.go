package Totoro

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"sort"
)

type RecItem struct {
	GroupName string
	TableName string
	Item      []string
}

type Record struct {
	//Groups map[string]int
	Recs []RecItem
}

func (rec *Record) Len() int {
	return len(rec.Recs)
}

func (rec *Record) Swap(i, j int) {
	rec.Recs[i], rec.Recs[j] = rec.Recs[j], rec.Recs[i]
}

func (rec *Record) Less(i, j int) bool {
	return rec.Recs[i].GroupName < rec.Recs[j].GroupName
}

func (rec *Record) RecSort() {
	sort.Sort(rec)
}

func (rec *Record) RecAppend(item RecItem) {
	/*
		gName := item.GroupName
			_, ok := rec.Groups[gName]
			if ok {
				rec.Groups[gName]++
			} else {
				rec.Groups[gName] = 1
			}
	*/

	rec.Recs = append(rec.Recs, item)
}

func (rec *Record) RecGetDB(dbName string, tableName ...string) {
	db, err := sql.Open("mysql", dbName)
	defer db.Close()

	err = db.Ping()
	if err != nil {
		fmt.Println("Ping Err")
		return
	}

	for _, tname := range tableName {
		rows, err := db.Query("select * from " + tname)
		if err != nil {
			fmt.Println("Table " + tname + " err")
			continue
		}
		cols, _ := rows.Columns()
		ln := len(cols)

		for rows.Next() {
			recItem := RecItem{GroupName: "ALL", TableName: tname, Item: make([]string, ln)}
			interfaceTmp := make([]interface{}, ln)
			for i := 0; i < ln; i++ {
				interfaceTmp[i] = &recItem.Item[i]
			}
			rows.Scan(interfaceTmp...)
			rec.RecAppend(recItem)
		}
	}
}
