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

    private int show_hours = 24 * 3;

    private boolean offline = false;

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

    protected void writeTable(Elements tab){
        this.table = new Table();
        Elements rows = tab.get(0).getElementsByClass("hour-row");
        for (int i = 3; i < show_hours && i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            ArrayList<String> rowStr = new ArrayList<>();
            ArrayList<String> rowCol = new ArrayList<>();
            if(cols.size() < 12){
                continue;
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
        String url;
        try {
            String location = viewModel.getLocation().getValue(); //получаем локацию(координаты)
            url = "https://www.meteoblue.com/en/weather/outdoorsports/seeing/" + location;
            //составляем ссылку
            document = Jsoup.connect(url).get();// Коннектимся и получаем страницу
            answer = document.body().html();// Получаем код из тега body страницы
            Elements tab = document.getElementsByClass("table-seeing");
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
