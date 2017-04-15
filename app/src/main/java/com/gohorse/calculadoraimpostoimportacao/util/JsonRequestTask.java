package com.gohorse.calculadoraimpostoimportacao.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class JsonRequestTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private Context mContext;
    private AsyncTaskCompleteListener cb;

    private final int CONN_TIMEOUT = 3000;
    private final int CONN_JSON_READ_TIMEOUT = 3000;

    public JsonRequestTask(Context context, AsyncTaskCompleteListener callback) {
        mContext = context;
        cb = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        iniciaProgressDialog();

    }

    /*
     * Faz a conexão http a partir da URL e monta a string Json.
     */
    @Override
    protected String doInBackground(String... uris) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(uris[0]);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(CONN_TIMEOUT);
            httpURLConnection.setReadTimeout(CONN_JSON_READ_TIMEOUT);

            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            br.close();

        } catch (IOException e) {
            Log.e("Leitura de dados", "Erro na leitura dos dados" );
            e.printStackTrace();

        } finally {

            if (httpURLConnection != null ) {
                httpURLConnection.disconnect();
            }

        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s != null) {
            cb.onTaskComplete(s);
        }

        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    private void iniciaProgressDialog() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Atualizando cotação...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}


