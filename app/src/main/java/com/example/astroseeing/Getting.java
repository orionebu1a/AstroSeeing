package com.example.astroseeing;

import android.os.AsyncTask;

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
    private ArrayList<ArrayList<String>> table = new ArrayList<>();

    private MyViewModel viewModel;

    private int show_hours = 1;

    private boolean offline = false;

    public Table getTable() {
        Table postTable = new Table(table);
        return postTable;
    }

    public void setTable(ArrayList<ArrayList<String>> table) {
        this.table = table;
    }

    public Getting(){
        super();
    }

    public Getting(MyViewModel viewModel) {
        super();
        this.viewModel = viewModel;
    }

    public Getting(ArrayList<ArrayList<String>> table) {
        super();
        this.table = table;
    }

    protected void writeTable(Element tab){
        Elements rows = tab.select("tr");
        for (int i = 3; i < show_hours + 3; i++) { //first row is the col names so skip it.
            Element row = rows.get(i);
            Elements cols = row.select("td");
            ArrayList<String> rowStr = new ArrayList<>();
            for (int j = 1; j <= 12; j++) {
                cols.get(j).text();
                rowStr.add(cols.get(j).text());
            }
            this.table.add(rowStr);
        }
        Table tablePacked = new Table(this.table);
        this.viewModel.select(tablePacked);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String answer = "";
        String url = "https://www.meteoblue.com/en/weather/outdoorsports/seeing/basel_switzerland_2661604";
        Document document = null;
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
