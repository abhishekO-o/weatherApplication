package com.example.whats_the_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherview,tphview;
    ImageView imageView;


    public void searchWeather(View view) {
        weatherview.setText("");
        tphview.setText("");
        imageView.animate().alpha(1f);
        DownloadTask task = new DownloadTask();
        try {
            String string = cityName.getText().toString();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + string + "&appid=d572a527669bc32de8a4e948ba1e42dd");
        }
        catch (Exception e){
            Toast.makeText(this,"Please enter valid  City name",Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);

    }
public class DownloadTask extends AsyncTask<String,Void,String>{
    @Override
    protected String doInBackground(String... urls) {
        String result="";
        URL url;
        HttpURLConnection connection=null;
        try {
            url=new URL(urls[0]);
            connection=(HttpURLConnection)url.openConnection();
            InputStream in=connection.getInputStream();
            InputStreamReader reader=new InputStreamReader(in);
            int data=reader.read();
            while (data!=-1){
                 char cc=(char)data;
                 result +=cc;
                 data=reader.read();
            }
            return result;

        } catch (Exception e) {
           e.printStackTrace();
            // Toast.makeText(getApplicationContext(),"Could not find Weather",Toast.LENGTH_SHORT).show();
        }
        return "display";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            String message1="";
            JSONObject jsonObject=new JSONObject(result);

            String weathermain =jsonObject.getString("weather");
            String temp_pres_hum=jsonObject.getString   ("main");

            tphview.setText(temp_pres_hum);

             Log.i("weatherContent",weathermain);
             Log.i("weatherContent",temp_pres_hum);

            JSONArray arr1=new JSONArray(weathermain);

            JSONObject obj=new JSONObject(temp_pres_hum);

            float temp= (float) obj.getDouble("temp");
            temp= (float) (temp-273.15);

            float min_temp=(float)obj.getDouble("temp_min");
            float max_temp=(float) obj.getDouble("temp_max");
            min_temp=(int)(min_temp-273.15);
            max_temp=(int)(max_temp-273.15);

            String press=obj.getString("pressure");
            String humid=obj.getString("humidity");
            String temp_pres_humid="Temperature : "+temp+" °C\r\nmin & max temp : "+min_temp+"°C - "+max_temp+"°C\r\n\r\nPressure : "+press+" mmHg\r\nHumidity : "+humid;
            tphview.setText(temp_pres_humid);
            for(int i=0;i<arr1.length();i++){
                JSONObject jsonpart1=arr1.getJSONObject(i);
                String main="";
                String describe="";
                String iconId="";
                main=jsonpart1.getString("main");
                describe=jsonpart1.getString("description");
                iconId=jsonpart1.getString("icon");
                Picasso.get().load(" http://openweathermap.org/img/wn/"+iconId+"@2x.png").into(imageView);
                Log.i("iconid",iconId);
                if(main!=""&&describe!=""){
                message1+="Weather : "+main+" \r\nDescription : "+describe+"\r\n";
                }
            }
            weatherview.setText(message1);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Details cannot be fetched",Toast.LENGTH_SHORT).show();
        }

    }
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName=(EditText)findViewById(R.id.cityName);
        weatherview=(TextView)findViewById(R.id.weatherview);
        tphview=(TextView)findViewById(R.id.tphview);
        imageView=(ImageView)findViewById(R.id.imageView);

    }
}
