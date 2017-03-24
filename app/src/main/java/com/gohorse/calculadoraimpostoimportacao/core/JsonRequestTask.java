package com.gohorse.calculadoraimpostoimportacao.core;

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

/**
 * Created by Lucas on 24/03/2017.
 */

public class JsonRequestTask extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... uris) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(uris[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            int code = httpURLConnection.getResponseCode();
            Log.e("response code: ", Integer.toString(code));

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
}


