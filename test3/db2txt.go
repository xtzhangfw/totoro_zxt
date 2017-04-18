package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"os"
)

func GetDBTables() {
	db, err := sql.Open("mysql", "payment:payment@tcp(127.0.0.1:3306)/totoro")
	defer db.Close()
	err = db.Ping()
	if err != nil {
		fmt.Println("Ping err")
		return
	}
	rows, err := db.Query("show tables like 'totoro%'")
	defer rows.Close()

	for rows.Next() {
		tname := ""
		rows.Scan(&tname)

		fout, _ := os.Create("Tables/" + tname)
		defer fout.Close()

		tableRows, err := db.Query("select * from " + tname)
		if err != nil {
			fmt.Println("query " + tname + " err")
			continue
		}
		defer tableRows.Close()

		cols, _ := tableRows.Columns()
		ln := len(cols)

		for tableRows.Next() {
			tmp := make([]string, ln)
			tmpInterface := make([]interface{}, ln)
			for i := range tmp {
				tmpInterface[i] = &tmp[i]
			}
			tableRows.Scan(tmpInterface...)
			str := ""
			for i := range tmp {
				str += tmp[i] + "#"
			}
			fout.WriteString(str + "\n")
		}
	}
}

type TableInfo struct {
	TName string
	Info  [][]string
}

func GetDBTablesInfo() {
	var tableinfo []TableInfo
	db, err := sql.Open("mysql", "payment:payment@tcp(127.0.0.1:3306)/totoro")
	defer db.Close()
	err = db.Ping()
	if err != nil {
		fmt.Println("Ping err")
		return
	}
	rows, err := db.Query("show tables like 'totoro%'")
	defer rows.Close()

	fout, _ := os.Create("TableInfo.txt")
	defer fout.Close()

	for rows.Next() {
		tname := ""
		rows.Scan(&tname)

		tableRows, err := db.Query("select * from " + tname + " limit 10")
		if err != nil {
			fmt.Println("query " + tname + " err")
			continue
		}
		defer tableRows.Close()

		cols, _ := tableRows.Columns()
		ln := len(cols)

		var item TableInfo
		item.TName = tname
		head := make([]string, ln)
		for i := range cols {
			head[i] = cols[i]
		}
		item.Info = append(item.Info, head)

		for tableRows.Next() {
			tmp := make([]string, ln)
			tmpInterface := make([]interface{}, ln)
			for i := range tmp {
				tmpInterface[i] = &tmp[i]
			}
			tableRows.Scan(tmpInterface...)
			line := make([]string, 0)
			for i := range tmp {
				line = append(line, tmp[i])
			}
			item.Info = append(item.Info, line)
		}

		tableinfo = append(tableinfo, item)
	}
	str, _ := json.Marshal(tableinfo)
	fout.WriteString(string(str))
}

func main() {
	GetDBTables()
	GetDBTablesInfo()
}
