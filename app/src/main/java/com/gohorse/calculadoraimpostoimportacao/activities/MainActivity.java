package com.gohorse.calculadoraimpostoimportacao.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.core.JsonRequestHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView fonteCotacao;
    private TextView valorCotacao;
    private TextView ultimaAtualizacao;

    private JSONObject jsonObject = null;

    private Moeda moeda;

    private static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fonteCotacao = (TextView) findViewById(R.id.fonte_cotacao_id);
        valorCotacao = (TextView) findViewById(R.id.valor_cotacao_id);
        ultimaAtualizacao = (TextView) findViewById(R.id.ultima_att_id);


        try {
            JsonRequestHandler jsonRequestHandler = new JsonRequestHandler();
            moeda = jsonRequestHandler.montarObjeto();

            String cotacaoFormatada = "U$" + String.valueOf(moeda.getValor());

            fonteCotacao.setText( moeda.getFonte() );
            valorCotacao.setText( cotacaoFormatada );
            ultimaAtualizacao.setText( moeda.getUltimaConsulta() );


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Não foi possível obter o valor da cotação.", Toast.LENGTH_LONG).show();
        }


        //CRIAR JOBSCHEDULER


    }

}
