package com.gohorse.calculadoraimpostoimportacao.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.util.AsyncTaskCompleteListener;
import com.gohorse.calculadoraimpostoimportacao.util.JsonRequestHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;
import com.gohorse.calculadoraimpostoimportacao.util.JsonRequestTask;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    private Toolbar toolbar;
    private TextView valorProdutoBrl;
    private TextView valorImportacaoBrl;
    private TextView valorIcmsBrl;
    private TextView valorTotalBrl;
    private TextView valorIcms;
    private TextView valorFreteBrl;

    private EditText valorCotacao;
    private EditText valorProdutoUsd;
    private EditText valorFreteUsd;

    private Spinner spinnerEstados;
    private Button btCalcular;
    private ScrollView scrollView;

    private JsonRequestHandler jsonRequestHandler;

    private Moeda moeda = null;

    private SimpleMaskFormatter maskFormatter = new SimpleMaskFormatter("N.NN");
    private MaskTextWatcher maskTextWatcher;

    private static final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        valorProdutoBrl = (TextView) findViewById(R.id.tv_valor_prod_brl_id);
        valorImportacaoBrl = (TextView) findViewById(R.id.tv_valor_importacao_id);
        valorIcmsBrl = (TextView) findViewById(R.id.tv_valor_icms_id);
        valorTotalBrl = (TextView) findViewById(R.id.tv_valor_total_id);
        valorIcms = (TextView) findViewById(R.id.tv_icms_id);
        valorFreteBrl = (TextView) findViewById(R.id.tv_valor_frete_brl_id);

        valorCotacao = (EditText) findViewById(R.id.et_valor_cotacao_id);
        valorProdutoUsd = (EditText) findViewById(R.id.et_valor_prod_brl_id);
        valorFreteUsd = (EditText) findViewById(R.id.et_valor_frete_id);
        spinnerEstados = (Spinner) findViewById(R.id.sp_estados_id);
        btCalcular = (Button) findViewById(R.id.bt_calcular_id);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_id);

        toolbar.setTitle("CIMP");
        setSupportActionBar(toolbar);

        jsonRequestHandler = new JsonRequestHandler();

        maskTextWatcher = new MaskTextWatcher(valorCotacao, maskFormatter);
        valorCotacao.addTextChangedListener(maskTextWatcher);


        if (!buscaCotacaoOnline()) {
            /*
             * Se algo der errado, o valor da TextView é passa a ser 0.00.
             */
            valorCotacao.setText("000");
        }

        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Atualiza a TextView ICMS de acordo com o estado selecionado;
                 * N/A - Não se Aplica
                 */
                String estado = spinnerEstados.getItemAtPosition(position).toString();

                if (!estado.equals("N/A")) {
                    int icms = jsonRequestHandler.recuperaIcmsEstados(getApplicationContext(), estado);
                    valorIcms.setText(String.valueOf(icms));

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
                /*
                 * Fecha o teclado caso esteja aberto.
                 */
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });


        btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valorCotacao.getText().toString().equals("") || valorProdutoUsd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos necessarios!", Toast.LENGTH_SHORT).show();

                } else {
                    calculaValores();
                    /*
                     * Animação de scroll (1 segundo) até a TextView valorTotalBrl.
                     */
                    ObjectAnimator.ofInt(scrollView, "ScrollY", valorTotalBrl.getBottom()).setDuration(1000).start();
                }

            }
        });

        btCalcular.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                 * Fecha o spinner/teclado caso esteja aberto.
                 */
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sobre_id:
                Intent intent = new Intent(getApplicationContext(), SobreActivity.class);
                startActivity(intent);
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


    /*
     * Faz o cálculo dos valores digitados pelo usuário.
     */
    private void calculaValores() {

        double valorCotacaoDolar;
        double produtoUsd;
        double freteUsd = 0;
        double icmsEstado = 0;

        valorCotacaoDolar = Double.parseDouble(valorCotacao.getText().toString());
        produtoUsd = Double.parseDouble(valorProdutoUsd.getText().toString());

        if (!valorFreteUsd.getText().toString().equals("")) {
            freteUsd = Double.parseDouble(valorFreteUsd.getText().toString());
        }

        if (!valorIcms.getText().toString().equals("N/A")) {
            icmsEstado = Double.parseDouble(valorIcms.getText().toString()) / 100;
        }

        double freteBrl = freteUsd * valorCotacaoDolar;
        double produtoBrl = produtoUsd * valorCotacaoDolar;
        double importacao = (freteBrl + produtoBrl) * 0.6;
        double baseCalculo1 = produtoBrl + importacao + freteBrl;
        double baseCalculo2 = baseCalculo1 / (1 - icmsEstado);
        double icms = baseCalculo2 * icmsEstado;
        double total = baseCalculo1 + icms;

        String strProdutoBrl = String.format(Locale.US, "R$ %.2f", produtoBrl);
        String strFreteBrl = String.format(Locale.US, "R$ %.2f", freteBrl);
        String strImportacao = String.format(Locale.US, "R$ %.2f", importacao);
        String strIcms = String.format(Locale.US, "R$ %.2f", icms);
        String strTotal = String.format(Locale.US, "R$ %.2f", total);

        valorProdutoBrl.setText(strProdutoBrl);
        valorFreteBrl.setText(strFreteBrl);
        valorIcmsBrl.setText(strIcms);
        valorImportacaoBrl.setText(strImportacao);
        valorTotalBrl.setText(strTotal);

    }

    /*
     * Verifica se há acesso a internet disponível no dispositivo.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
     * Retorna TRUE se a AsyncTask for iniciada.
     * Retorna FALSE se acontecer algum problema.
     */
    private boolean buscaCotacaoOnline() {

            if (isNetworkAvailable()) {

                JsonRequestTask jsonRequestTask = new JsonRequestTask(MainActivity.this, this);
                jsonRequestTask.execute(URL_STRING);

                return true;

            } else {
                Toast.makeText(getApplicationContext(),
                        "Não foi possível obter a cotação online. Verifique a sua conexão ou insira o valor manualmente.",
                        Toast.LENGTH_LONG).show();

                return false;
            }

    }

    /*
     * Metodo executado após AsyncTask concluir.
     * Se acontecer algum problema na hora de buscar a cotação, o valor da TextView passa a ser 0.00.
     */
    @Override
    public void onTaskComplete(String result) {

        moeda = jsonRequestHandler.montarObjeto(result);

        if (moeda != null) {
            String valorMoeda = String.valueOf(moeda.getValor());
            valorCotacao.setText(valorMoeda);

        } else {
            valorCotacao.setText("000");
        }
    }


}
