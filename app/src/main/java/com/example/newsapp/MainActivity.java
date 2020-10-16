package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   DatabaseHelper db;
   Cursor cursor;
   ArrayList<String> title;
   ArrayList<String> link;
   ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview);
        db=new DatabaseHelper(this);
         DownloadTask task=new DownloadTask();
         task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
          cursor=db.viewdata();
        title=new ArrayList<>();
        link=new ArrayList<>();
        while(cursor.moveToNext())
        {
            title.add(cursor.getString(0));
            link.add(cursor.getString(1));//0 for title 1 for link
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,title);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),webview.class);
                intent.putExtra("link",link.get(i));
                startActivity(intent);
            }

        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String share=link.get(i);
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,share);
                Intent chooser=intent.createChooser(intent,"Share this link");
                startActivity(chooser);
                return false;
            }
        });

    }
   public class DownloadTask extends AsyncTask<String, Void,String>
   {

       @Override
       protected String doInBackground(String... urls) {

           String result="";
           URL url;
           HttpURLConnection urlConnection=null;
           try{
               url=new URL(urls[0]);
               urlConnection=(HttpURLConnection) url.openConnection();
               InputStream inputStream=urlConnection.getInputStream();
               InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
               int data=inputStreamReader.read();
               while(data!=-1)
               {
                   char current=(char) data;
                   result+=current;
                   data=inputStreamReader.read();
               }
                Log.i("resources",result);
                JSONArray jsonArray=new JSONArray(result);
                int n=20;
                if(jsonArray.length()<20)
                {
                    n=jsonArray.length();
                }
               db.delete();
                for(int i=0;i<n;i++)
                {
                    String articleid=jsonArray.getString(i);
                    url=new URL("https://hacker-news.firebaseio.com/v0/item/"+ articleid +".json?print=pretty");
                    String articleinfo="";
                    urlConnection=(HttpURLConnection) url.openConnection();
                     inputStream=urlConnection.getInputStream();
                     inputStreamReader=new InputStreamReader(inputStream);
                     data=inputStreamReader.read();
                    while(data!=-1)
                    {
                        char current=(char) data;
                        articleinfo+=current;
                        data=inputStreamReader.read();
                    }


                    JSONObject jsonObject=new JSONObject((articleinfo));
                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url"))
                    {
                        String articletitle=jsonObject.getString("title");
                        String articleurl=jsonObject.getString("url");
                        Log.i("url",articleurl);
                        db.addData(articletitle,articleurl);
                    }
                }

               return result;
           }catch(Exception e)
           {
               e.printStackTrace();
           }

           return null;
       }
   }
}