package com.gohorse.calculadoraimpostoimportacao.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.core.JsonRequestTask;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView teste;

    private JsonRequestTask jsonRequestTask;
    private JSONObject jsonObject = null;

    private Moeda moeda;

    public static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teste = (TextView) findViewById(R.id.testeTextView);


        try {

            String jsonString = new JsonRequestTask().execute(URL_STRING).get();
            jsonObject = new JSONObject( jsonString );

            teste.setText( jsonObject.getString("status").toString() );

        } catch (Exception e) {
            e.printStackTrace();

        }

        //CRIAR JOBSCHEDULER


    }

}
