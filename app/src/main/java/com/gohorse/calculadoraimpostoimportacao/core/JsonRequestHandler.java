package com.gohorse.calculadoraimpostoimportacao.core;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * Created by Lucas on 24/03/2017.
 */

public class JsonRequestHandler {

    private JSONObject jsonObject = null;
    private Moeda moedaUSD;

    private Timestamp timestamp;

    private static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    //Monta o objeto Moeda a partir do JSON
    public Moeda montarObjeto() {

        try {
            String jsonString = new JsonRequestTask().execute(URL_STRING).get();
            jsonObject = new JSONObject(jsonString);

            String nome = jsonObject.getJSONObject("valores").getJSONObject("USD").getString("nome");
            float valor = Float.parseFloat( jsonObject.getJSONObject("valores").getJSONObject("USD").getString("valor") );
            String fonte = jsonObject.getJSONObject("valores").getJSONObject("USD").getString("fonte");
            String ultimConsulta =  jsonObject.getJSONObject("valores").getJSONObject("USD").getString("ultima_consulta");

            moedaUSD = new Moeda(nome, valor, timeStampConverter(ultimConsulta), fonte);

            return moedaUSD;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //Converte a string Timestap para uma data legível
    private String timeStampConverter(String timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Timestamp ts = new Timestamp( (Long.parseLong(timestamp)) * 1000 ); //1 segundo = 1000 milisegundos
        Date date = new Date( ts.getTime() );
        String data = formatter.format(date);

        return data;
    }


}
