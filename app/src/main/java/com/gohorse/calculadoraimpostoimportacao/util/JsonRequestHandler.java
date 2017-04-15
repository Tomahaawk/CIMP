package com.gohorse.calculadoraimpostoimportacao.util;

import android.content.Context;
import android.util.Log;

import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class JsonRequestHandler {

    private JSONObject jsonObject = null;
    private Moeda moedaUSD;

    public JsonRequestHandler() {}

    /*
     * Monta o objeto Moeda a partir do JSON
     */
    public Moeda montarObjeto(String jsonString) {


        try {
            jsonObject = new JSONObject(jsonString);

            String nome = jsonObject.getJSONObject("valores").getJSONObject("USD").getString("nome");
            float valor = Float.parseFloat( jsonObject.getJSONObject("valores").getJSONObject("USD").getString("valor") );
            String fonte = jsonObject.getJSONObject("valores").getJSONObject("USD").getString("fonte");
            String ultimConsulta =  jsonObject.getJSONObject("valores").getJSONObject("USD").getString("ultima_consulta");

            moedaUSD = new Moeda(nome, valor, timeStampConverter(ultimConsulta), fonte);

        }  catch (JSONException e) {
            Log.e("Erro: ", "Falha ao ler JSON");
            e.printStackTrace();

        }

        return moedaUSD;

    }

    /*
     * Converte a string Timestap para uma data legível.
     */
    private String timeStampConverter(String timestamp) {

        String template = "dd/MM/yyyy - HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.US);
        Timestamp ts = new Timestamp( (Long.parseLong(timestamp)) * 1000 ); //1 segundo = 1000 milisegundos
        Date date = new Date( ts.getTime() );
        //String data = formatter.format(date);

        return formatter.format(date);
    }

    /*
     * Busca o ICMS do estado no JSON de acordo com a seleção do spinner.
     */
    public int recuperaIcmsEstados(Context context, String chave_estado) {

        int icms_estado = 0;
        StringBuilder sb = new StringBuilder();

        try {
            //Lê o arquivo JSON icms_estados
            InputStream in = context.getResources().openRawResource(R.raw.icms_estados);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

            br.close();

            jsonObject = new JSONObject(sb.toString());
            icms_estado = Integer.parseInt( jsonObject.getJSONObject("estado").getString(chave_estado) );


        } catch (Exception e) {
            e.printStackTrace();

        }

        return icms_estado;
    }

}
