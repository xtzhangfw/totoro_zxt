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

type Info struct {
	Advertiser string
	Revenue    [12]float64
}

type InfoList struct {
	Data []Info
}

func Handler(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", "payment:payment@tcp(127.0.0.1:3306)/totoro")
	//db, err := sql.Open("mysql", "payment:payment@tcp(192.168.0.64:3306)/totoro")
	if err != nil {
		fmt.Println("open err")
	}
	defer db.Close()

	err = db.Ping()
	if err != nil {
		fmt.Println("Ping err2")
	}

	rows, err := db.Query("select advertiser,bill_revenue,month from totoro_advertiser_revenue_stas where year=2016")
	if err != nil {
		fmt.Println("query err")
	}

	defer rows.Close()

	var data InfoList
	data.Data = make([]Info, 0)
	mp := make(map[string]int, 0)

	for rows.Next() {
		var (
			name    string
			month   int
			revenue float64
		)

		err := rows.Scan(&name, &revenue, &month)
		if err != nil {
			fmt.Println("rows error")
		}
		index, ok := mp[name]
		if !ok {
			index = len(data.Data)
			mp[name] = index
			data.Data = append(data.Data, Info{Advertiser: name})
		}
		data.Data[index].Revenue[month-1] = revenue
	}

	t, err := template.ParseFiles("revenue.html")
	data.Data = data.Data[:50]
	err = t.Execute(w, data)
}

func main() {
	http.HandleFunc("/", Handler)
	fs := http.FileServer(http.Dir("/home/zxt/totoro_zxt/static/"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))
	http.ListenAndServe(":8888", nil)
}
