package com.gohorse.calculadoraimpostoimportacao.util;

import android.os.AsyncTask;
import android.util.IntProperty;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class JsonRequestTask extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... uris) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(uris[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            httpURLConnection.disconnect();
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


    }
}


