package com.gohorse.calculadoraimpostoimportacao.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.util.JsonRequestHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView valorProdutoBrl;
    private TextView valorImportacaoBrl;
    private TextView valorIcmsBrl;
    private TextView valorTotalBrl;
    private TextView valorIcms;
    private TextView valorImpostoImportacao;

    private EditText valorCotacao;
    private EditText valorProdutoUsd;
    private Spinner spinnerEstados;

    private JsonRequestHandler jsonRequestHandler;

    private Moeda moeda;

    private SimpleMaskFormatter maskFormatter = new SimpleMaskFormatter("N.NN");
    private MaskTextWatcher maskTextWatcher;


    //CRIAR JOBSCHEDULER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        valorProdutoBrl = (TextView) findViewById(R.id.tv_valor_prod_brl_id);
        valorImportacaoBrl = (TextView) findViewById(R.id.tv_valor_importacao_id);
        valorIcmsBrl = (TextView) findViewById(R.id.tv_valor_icms_id);
        valorTotalBrl = (TextView) findViewById(R.id.tv_valor_total_id);
        valorImpostoImportacao = (TextView) findViewById(R.id.tv_imposto_id);
        valorIcms = (TextView) findViewById(R.id.tv_icms_id);

        valorCotacao = (EditText) findViewById(R.id.et_valor_cotacao_id);
        valorProdutoUsd = (EditText) findViewById(R.id.et_valor_prod_brl_id);
        spinnerEstados = (Spinner) findViewById(R.id.sp_estados_id);

        toolbar.setTitle("CIP");
        setSupportActionBar(toolbar);


        try {
            jsonRequestHandler = new JsonRequestHandler();
            moeda = jsonRequestHandler.montarObjeto();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Não foi possível obter o valor da cotação.", Toast.LENGTH_LONG).show();
        }

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
        spinnerEstados.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm =(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valorCotacao.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(valorProdutoUsd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(valorIcms.getWindowToken(), 0);
                return false;
            }
        });

        maskTextWatcher = new MaskTextWatcher(valorCotacao, maskFormatter);
        valorCotacao.addTextChangedListener(maskTextWatcher);

        String valorMoeda = String.valueOf( moeda.getValor() );
        valorCotacao.setText(valorMoeda);


        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event == null) {
                    calculaValores();
                    return false;
                }
                return true;
            }
        };
        valorProdutoUsd.setOnEditorActionListener(exampleListener);



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

    private void calculaValores() {

        double valorCotacaoDolar = Double.parseDouble( valorCotacao.getText().toString() );
        double icmsEstado = 0;

        if(!valorIcms.getText().toString().equals("N/A")) {
            icmsEstado = Double.parseDouble(valorIcms.getText().toString()) / 100;
        }

        double produtoUsd = Double.parseDouble( valorProdutoUsd.getText().toString() );
        double produtoBrl = produtoUsd * valorCotacaoDolar;
        double importacao = produtoBrl * 0.6;
        double baseCalculo1 = produtoBrl + importacao;
        double baseCalculo2 = baseCalculo1 / (1 - icmsEstado);
        double icms = baseCalculo2 * icmsEstado ;
        double total = baseCalculo1 + icms;

        String strProdutoBrl = String.format(Locale.US, "R$ %.2f", produtoBrl);
        String strImportacao = String.format(Locale.US, "R$ %.2f", total);
        String strIcms = String.format(Locale.US, "R$ %.2f", icms);
        String strTotal = String.format(Locale.US, "R$ %.2f", total);

        valorProdutoBrl.setText(strProdutoBrl);
        valorIcmsBrl.setText(strIcms);
        valorImportacaoBrl.setText(strImportacao);
        valorTotalBrl.setText(strTotal);

    }

}
