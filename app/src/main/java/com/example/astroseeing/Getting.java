package com.example.astroseeing;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Getting extends AsyncTask<String, String, String> {
    private Table table;

    private MyViewModel viewModel;

    private int show_hours = 1;

    private boolean offline = false;

    /*public Table getTable() {
        Table postTable = new Table(table);
        return postTable;
    }*/

    public void setTable(Table table) {
        this.table = table;
    }

    public Getting(){
        super();
    }

    public Getting(MyViewModel viewModel) {
        super();
        this.viewModel = viewModel;
    }

    protected void writeTable(Element tab){
        this.table = new Table();
        Elements rows = tab.select("tr");
        for (int i = 3; i < show_hours + 3; i++) { //first row is the col names so skip it.
            Element row = rows.get(i);
            Elements cols = row.select("td");
            ArrayList<String> rowStr = new ArrayList<>();
            ArrayList<String> rowCol = new ArrayList<>();
            for (int j = 1; j <= 12; j++) {
                String style = cols.get(j).attr("style");
                if(cols.get(j).hasAttr("style")){
                    String color = style.split(";")[0].split(" ")[1];
                    rowCol.add(color);
                }
                else{
                    rowCol.add("#fff");
                }
                rowStr.add(cols.get(j).text());
            }
            this.table.data.add(rowStr);
            this.table.colors.add(rowCol);
        }
        this.viewModel.select(table);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String answer = "";
        MutableLiveData<String> a = viewModel.getPlace();
        String place = a.getValue();
        Document document;
        String url = "https://www.meteoblue.com/en/weather/outdoorsports/seeing/" + place;

        try {
            document = Jsoup.connect(url).get();// Коннектимся и получаем страницу
            answer = document.body().html();// Получаем код из тега body страницы
            Element tab = document.getElementsByClass("table-seeing").get(0);
            writeTable(tab);
        } catch (IOException e) {
        }
        return answer;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
