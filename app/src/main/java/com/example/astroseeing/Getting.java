package com.example.astroseeing;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import org.jsoup.Connection;
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

    private int show_hours = 24;

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
        for (int i = 3; i < show_hours * 11 + 3; i = i + 11) { //first row is the col names so skip it.
            Element row = rows.get(i);
            Elements cols = row.select("td");
            ArrayList<String> rowStr = new ArrayList<>();
            ArrayList<String> rowCol = new ArrayList<>();
            if(cols.size() < 12){
                break;
            }
            for (int j = 0; j <= 12; j++) {
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
        MutableLiveData<String> placeBuf = viewModel.getPlace();
        String place = placeBuf.getValue();
        Document document;
        /*String preUrl = "https://yandex.ru/search/?text=" + place + "+" + "meteoblue" + "astronomical" + "seeing";
        //preUrl = "https://www.youtube.com";
        String href = "https://www.meteoblue.com/ru/%D0%BF%D0%BE%D0%B3%D0%BE%D0%B4%D0%B0/outdoorsports/seeing/%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0_%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F_524901";;
        try {
            Connection jsoup = Jsoup.connect(preUrl);
            document = jsoup.get();
            answer = document.body().html();// Получаем код из тега body страницы
            Element preres = document.getElementsByClass("serp-item__title").get(0);
            //Element ref = res.getElementsByTag("href").get(0);
            Element res = preres.getElementsByAttribute("href").get(0);
            href = res.attr("href");
            int b = 0;
        } catch (Exception e) {
            System.out.println("not");
        }

        //String url = a.getValue();
        String url = href;*/
        String url;
        try {
            String location = viewModel.getLocation().getValue();
            url = "https://www.meteoblue.com/en/weather/outdoorsports/seeing/" + location;
            document = Jsoup.connect(url).get();// Коннектимся и получаем страницу
            answer = document.body().html();// Получаем код из тега body страницы
            Element tab = document.getElementsByClass("table-seeing").get(0);
            writeTable(tab);
        } catch (Exception e) {

        }
        return answer;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
