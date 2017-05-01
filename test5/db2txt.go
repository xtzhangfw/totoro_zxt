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

func GetDBTables(dbinfo string, querys string, fname string) {
	db, err := sql.Open("mysql", dbinfo)
	defer db.Close()
	err = db.Ping()
	if err != nil {
		fmt.Println("Ping err")
		return
	}

	fout, _ := os.Create(fname)

	rows, err := db.Query(querys)
	defer rows.Close()
	cols, _ := rows.Columns()
	ln := len(cols)
	num := 0

	space := string(rune(1))

	fout.WriteString("Placement Table\n")
	fout.WriteString("network_id" + space + "start_date" + space + "end_date" + space + "budget_model" + space + "placement_type" + space + "price_model" + space + "billing_method" + space + "controlling_measure" + "\n")
	fout.WriteString("INT" + space + "DATE" + space + "DATE" + space + "STRING" + space + "STRING" + space + "STRING" + space + "STRING" + space + "STRING" + "\n")
	for rows.Next() {
		fmt.Println(num)
		num = num + 1
		tmp := make([]string, ln)
		tmpInterface := make([]interface{}, ln)
		for i := range tmp {
			tmpInterface[i] = &tmp[i]
		}
		rows.Scan(tmpInterface...)
		str := ""
		for i := range tmp {
			str += tmp[i] + space
		}
		fout.WriteString(str + "\n")
	}

}

func main() {
	GetDBTables("zxt:t@tcp(162.105.147.185:3306)/fwrpm_rpt", "SELECT network_id,start_date,end_date,budget_model,placement_type,price_model,billing_method,controlling_measure FROM d_placement where start_date is not NULL", "Tables/d_placement")
}
