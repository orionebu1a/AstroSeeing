package com.example.astroseeing;

import static android.content.ContentValues.TAG;
import static androidx.navigation.ActivityKt.findNavController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public boolean offline;
    public String request;
    public String WeekNumber;
    public int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Getting getting = new Getting();
        getting.execute();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openFragment(Home.newInstance("",""));
                        return true;
                    case R.id.navigation_seeing:
                        openFragment(Light_polution.newInstance("",""));
                        return true;
                    case R.id.navigation_light_pollution:
                        openFragment(Seeing.newInstance("",""));
                        return true;
                }
                return false;
            }
        });
    }
    private void openFragment(Fragment fragment) {
        Log.d(TAG, "openFragment: ");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //this is a helper class that replaces the container with the fragment. You can replace or add fragments.
        transaction.replace(R.id.fragmentContainerView2, fragment);
        transaction.addToBackStack(null); //if you add fragments it will be added to the backStack. If you replace the fragment it will add only the last fragment
        transaction.commit(); // commit() performs the action
    }

    class Getting extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //В этом методе код перед началом выполнения фонового процесса
        }

        @Override
        protected String doInBackground(String... params) {
            /*Этот метод выполняется в фоне
            Тут мы обращаемся к сайту и вытаскиваем его html код
            */
            String answer = "";// В эту переменную мы будем класть ответ от сайта. Пока что она пустая
            String url = "https://www.meteoblue.com/en/weather/outdoorsports/seeing/basel_switzerland_2661604";// Адрес сайта с расписанием
            Document document = null;
            try {
                document = Jsoup.connect(url).get();// Коннектимся и получаем страницу
                answer = document.body().html();// Получаем код из тега body страницы
                Elements lines = document.getElementsByAttributeValue("class", "hour-row night");
                LinkedList<String> htmlLines = new LinkedList<>();
                //System.out.println(lines.get(1).html());
                for(int i = 0; i < lines.size(); i++){
                    htmlLines.add(lines.get(i).html());
                    System.out.println(htmlLines.get(i));
                }

                //for()


            } catch (IOException e) {
                // Если произошла ошибка, значит вероятнее всего, отсутствует соединение с интернетом
                // Загружаем в переменную answer оффлайн версию из txt файла
                try {
                    BufferedReader read = new BufferedReader(new InputStreamReader(openFileInput("timetable.txt")));
                    String str = "";
                    while ((str = read.readLine())!=null){
                        answer +=str;
                    }
                    read.close();
                    offline = true;//работаем в оффлайн режиме
                } catch (FileNotFoundException ex) {
                    //Если файла с сохранённым расписанием нет, то записываем в answer пустоту
                    answer = "";
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return answer;//Вытаскиваем текст из кода в переменной answer и передаём в UI-поток
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*Этот метод выполняется при завершении фонового кода
            Сюда возвращаются данные из потока
             */
            request = "";//Начинаем формировать ответ
            String temp = result.toString();//Делаём временную строку
            // Записываем содержимое, в файл timetable.txt, в котором будем хранить оффлайн версию расписания
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(openFileOutput("lastseeing.txt",MODE_PRIVATE)));
                writer.write(temp);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}