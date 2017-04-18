package main

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	//"io/ioutil"
	"net/http"
	//"os"
	"html/template"
)

var dbinfo DBInfo
var transdata TransData

type TableInfo struct {
	TableName string
	Field     []string
	Type      []string
}

type DBInfo struct {
	TableNum int
	Tables   []TableInfo
}

type TransData struct {
	TableList []string
}

func CreateTransData() {
	GetDBInfo(&dbinfo)
	transdata.TableList = make([]string, dbinfo.TableNum)
	for i := range transdata.TableList {
		transdata.TableList[i] = dbinfo.Tables[i].TableName
	}
}

func GetDBInfo(dbinfo *DBInfo) {
	db, err := sql.Open("mysql", "payment:payment@tcp(127.0.0.1:3306)/totoro")
	defer db.Close()
	err = db.Ping()
	if err != nil {
		fmt.Println("Ping err2")
		return
	}
	rows, err := db.Query("show tables like 'totoro%'")
	defer rows.Close()

	dbinfo.TableNum = 0
	for rows.Next() {
		tname := ""
		rows.Scan(&tname)
		tinfo := TableInfo{TableName: tname, Field: make([]string, 0), Type: make([]string, 0)}

		tableRows, err := db.Query("show columns from " + tname)
		if err != nil {
			fmt.Println("query " + tname + " err")
			continue
		}
		defer tableRows.Close()

		cols, _ := tableRows.Columns()
		ln := len(cols)

		for tableRows.Next() {
			tmp := make([]sql.RawBytes, ln)
			tmpInterface := make([]interface{}, ln)
			for i := range tmp {
				tmpInterface[i] = &tmp[i]
			}
			tableRows.Scan(tmpInterface...)
			tinfo.Field = append(tinfo.Field, string(tmp[0]))
			tinfo.Type = append(tinfo.Type, string(tmp[1]))
		}
		dbinfo.TableNum++
		dbinfo.Tables = append(dbinfo.Tables, tinfo)
	}
}

func IndexHandler(w http.ResponseWriter, r *http.Request) {
	t, err := template.ParseFiles("Template/index.html")
	if err != nil {
		fmt.Println("Parse err")
		return
	}
	t.Execute(w, transdata)

}

func main() {
	CreateTransData()
	fmt.Println(transdata)

	http.HandleFunc("/", IndexHandler)
	fs := http.FileServer(http.Dir("/home/zxt/totoro_zxt/test2/static/"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))
	http.ListenAndServe(":8888", nil)
}
