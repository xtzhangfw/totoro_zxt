package main

import (
	"database/sql"
	//	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"os"
)

var TypeMap map[string]string = map[string]string{
	"varchar":    "STRING",
	"bigint":     "INT",
	"longtext":   "STRING",
	"datetime":   "STRING",
	"int":        "INT",
	"tinyint":    "INT",
	"decimal":    "FLOAT",
	"double":     "FLOAT",
	"char":       "STRING",
	"timestamp":  "STRING",
	"set":        "STRING",
	"enum":       "STRING",
	"float":      "FLOAT",
	"longblob":   "STRING",
	"mediumtext": "STRING",
	"mediumblob": "STRING",
	"smallint":   "INT",
	"text":       "STRING",
	"blob":       "STRING",
	"time":       "STRING",
}

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

		infoOut, _ := os.Create("Tables/" + tname + ".info")
		defer infoOut.Close()

		infoRows, err := db.Query("SELECT column_name,data_type FROM information_schema.columns WHERE table_name='" + tname + "'")
		if err != nil {
			fmt.Println("query " + tname + " error")
			continue
		}

		num := 0
		for infoRows.Next() {
			colName, colType := "", ""
			infoRows.Scan(&colName, &colType)
			infoOut.WriteString(colName + fmt.Sprintf(" %d ", num) + TypeMap[colType] + "\n")
			num += 1
		}
	}
}

func main() {
	GetDBTables()
}
