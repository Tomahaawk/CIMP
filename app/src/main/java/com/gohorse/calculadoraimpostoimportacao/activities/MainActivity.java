package com.gohorse.calculadoraimpostoimportacao.activities;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.util.JsonRequestHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView valorCotacao;
    private TextView valorProdutoBrl;
    private TextView valorImportacaoBrl;
    private TextView valorIcmsBrl;
    private TextView valorTotalBrl;

    private EditText valorProdutoUsd;
    private EditText valorIcms;
    private Spinner spinnerEstados;


    private JSONObject jsonObject = null;
    private JsonRequestHandler jsonRequestHandler;

    private Moeda moeda;

    private static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        valorCotacao = (TextView) findViewById(R.id.valor_cotacao_id);
        valorProdutoBrl = (TextView) findViewById(R.id.tv_valor_prod_brl_id);
        valorImportacaoBrl = (TextView) findViewById(R.id.tv_valor_importacao_id);
        valorIcmsBrl = (TextView) findViewById(R.id.tv_valor_icms_id);
        valorTotalBrl = (TextView) findViewById(R.id.tv_valor_total_id);
        valorProdutoUsd = (EditText) findViewById(R.id.et_valor_prod_brl_id);
        valorIcms = (EditText) findViewById(R.id.et_icms_id);
        spinnerEstados = (Spinner) findViewById(R.id.sp_estados_id);

        toolbar.setTitle("CIP");
        setSupportActionBar(toolbar);


        try {
            jsonRequestHandler = new JsonRequestHandler();
            moeda = jsonRequestHandler.montarObjeto();

            DecimalFormat numberFormat = new DecimalFormat("#.00");
            String valorMoeda = numberFormat.format(moeda.getValor());
            String cotacaoFormatada = "U$" + valorMoeda;

            valorCotacao.setText( cotacaoFormatada );


            spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String estado = spinnerEstados.getItemAtPosition(position).toString();

                    if (!estado.equals("N/A")) {
                        int icms = jsonRequestHandler.recuperaIcmsEstados(getApplicationContext(), estado);
                        valorIcms.setText( String.valueOf(icms) );

                    } else {
                        valorIcms.setText("N/A");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Não foi possível obter o valor da cotação.", Toast.LENGTH_LONG).show();
        }

        //CRIAR JOBSCHEDULER

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sobre_id:
                return true;

            case R.id.menu_info_dolar:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
