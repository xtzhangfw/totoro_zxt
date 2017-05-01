package main

import (
	//	"encoding/json"
	"fmt"
	"html/template"
	//	"io/ioutil"
	"net/http"
	//	"os/exec"
	//	"strconv"
	//	"strings"
)

type TemplateData struct {
	TList []string
}

var templatedata TemplateData

func IndexHandler(w http.ResponseWriter, r *http.Request) {
	t, err := template.ParseFiles("templates/index.html")
	if err != nil {
		fmt.Println("Parse err")
		return
	}
	t.Execute(w, templatedata)
}

func main() {
	templatedata.TList = make([]string, 10)
	for i := 0; i < 10; i++ {
		str := fmt.Sprintf("Table %d", i)
		fmt.Println(str)
		templatedata.TList[i] = str
	}
	http.HandleFunc("/", IndexHandler)
	fs := http.FileServer(http.Dir("/home/zxt/IdeaProjects/totoro_zxt/test5/static/"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))
	http.ListenAndServe(":8888", nil)

}
