package main

import (
	"encoding/json"
	"fmt"
	"html/template"
	"io/ioutil"
	"net/http"
	"os/exec"
	"strconv"
	"strings"
)

type JsonItem struct {
	Group []string
	XY    [][]string
}

type TableInfo struct {
	TName string
	Info  [][]string
}

type PlotItem struct {
	Group []string
	XY    [][]float64
}

type TemplateData struct {
	TableList []TableInfo
	PlotList  []PlotItem
	JsonList  []JsonItem
}

var templatedata TemplateData

func GetTableInfo() {
	jsonStr, _ := ioutil.ReadFile("./TableInfo.txt")
	json.Unmarshal(jsonStr, &templatedata.TableList)
}

func Query(args ...string) {
	//cmd := exec.Command("./query.py", "totoro_time_revenue_stas", "3", "5", "1", "2")
	cmd := exec.Command("./query.py", args...)
	res, _ := cmd.Output()

	json.Unmarshal([]byte(res), &templatedata.JsonList)
	templatedata.PlotList = make([]PlotItem, len(templatedata.JsonList))

	for i := range templatedata.JsonList {
		templatedata.PlotList[i].Group = templatedata.JsonList[i].Group
		templatedata.PlotList[i].XY = make([][]float64, 0)
		for _, xystr := range templatedata.JsonList[i].XY {
			x, _ := strconv.ParseFloat(xystr[0], 64)
			y, _ := strconv.ParseFloat(xystr[1], 64)
			xy := make([]float64, 2)
			xy[0], xy[1] = x, y
			templatedata.PlotList[i].XY = append(templatedata.PlotList[i].XY, xy)
		}
	}

	/*
		for gID := range data {
			gName := strings.Join(data[gID].Group, "#")
			gData := data[gID].XY
			for _, xy := range gData {
				x, y := xy[0], xy[1]
				fmt.Println(gName, x, y)
			}
		}
	*/
}

func IndexHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method == "POST" {
		r.ParseForm()
		tname := r.Form["tname"][0]
		gcol := strings.Split(r.Form["gcol"][0], " ")
		xycol := strings.Split(r.Form["xycol"][0], " ")
		args := make([]string, 0)
		args = append(args, tname)
		for i := range xycol {
			args = append(args, xycol[i])
		}
		for i := range gcol {
			args = append(args, gcol[i])
		}
		Query(args...)
	}

	t, err := template.ParseFiles("Templates/index.html")
	if err != nil {
		fmt.Println("Parse err")
		return
	}
	t.Execute(w, templatedata)
}

func main() {
	GetTableInfo()
	//Query("totoro_time_revenue_stas", "3", "5", "1", "2")
	http.HandleFunc("/", IndexHandler)
	fs := http.FileServer(http.Dir("/home/hadoop/totoro_zxt/test3/static/"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))
	http.ListenAndServe(":8888", nil)

}
