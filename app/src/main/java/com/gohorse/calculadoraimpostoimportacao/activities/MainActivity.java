package com.gohorse.calculadoraimpostoimportacao.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.core.JsonRequestHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView teste;

    private JSONObject jsonObject = null;

    private Moeda moeda;

    private static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teste = (TextView) findViewById(R.id.testeTextView);

        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler();
        moeda = jsonRequestHandler.montarObjeto();

        teste.setText(moeda.getUltimaConsulta());


        //CRIAR JOBSCHEDULER


    }

}
