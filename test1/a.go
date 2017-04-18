package main

import (
	"fmt"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
)

func main() {
	db, err := sql.Open("mysql", "payment:payment@tcp(192.168.0.64:3306)/totoro")
	if err != nil {
		fmt.Println("open err")
	}
	defer db.Close()

	err = db.Ping()
	if err!= nil {
		fmt.Println("Ping err2")
	}

	rows, err := db.Query("select * from totoro_time_revenue_stas")
	if err != nil {
		fmt.Println("query err")
	}

	defer rows.Close()

	var (
		id, network_id, year, mouth, bill_event int 
		bill_revenue float32
	)

	for rows.Next() {
		err := rows.Scan(&id, &network_id, &year, &mouth, &bill_event, &bill_revenue)
		if err != nil {
			fmt.Println("rows error")
		}
		fmt.Println(id, network_id, year, mouth, bill_event, bill_revenue)
	}
}
