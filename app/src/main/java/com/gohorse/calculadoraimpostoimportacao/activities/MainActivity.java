package com.gohorse.calculadoraimpostoimportacao.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.gohorse.calculadoraimpostoimportacao.R;
import com.gohorse.calculadoraimpostoimportacao.interfaces.AsyncTaskCompleteListener;
import com.gohorse.calculadoraimpostoimportacao.util.JsonHandler;
import com.gohorse.calculadoraimpostoimportacao.core.Moeda;
import com.gohorse.calculadoraimpostoimportacao.util.JsonRequestTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    private View parentView; //Utilizado para criar snackbar.
    private Toolbar toolbar;
    private TextView valorProdutoBrl;
    private TextView valorImportacaoBrl;
    private TextView valorIcmsBrl;
    private TextView valorTotalBrl;
    private TextView valorIcms;
    private TextView valorFreteBrl;

    private TextInputLayout textInputLayoutProduto;
    private TextInputLayout textInputLayoutCotacao;

    private EditText valorCotacao;
    private EditText valorProdutoUsd;
    private EditText valorFreteUsd;

    private MaterialSpinner spinnerEstados;
    private Button btCalcular;
    private Button btLimpar;
    private ScrollView scrollView;

    private JsonHandler jsonHandler;

    private Moeda moeda = null;

    private final SimpleMaskFormatter maskFormatter = new SimpleMaskFormatter("N.NN");
    private MaskTextWatcher maskTextWatcher;

    private final Locale locale = new Locale("pt", "BR");
    private final String URL_STRING = "http://api.promasters.net.br/cotacao/v1/valores";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentView = findViewById(R.id.parent_view);
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

        textInputLayoutCotacao = (TextInputLayout) findViewById(R.id.tipl_valor_cotacao);
        textInputLayoutProduto = (TextInputLayout) findViewById(R.id.tipl_valor_produto);

        btCalcular = (Button) findViewById(R.id.bt_calcular_id);
        btLimpar = (Button) findViewById(R.id.bt_limpar_id);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_id);
        spinnerEstados = (MaterialSpinner) findViewById(R.id.sp_estados_id);

        toolbar.setTitle( getResources().getString(R.string.app_title) );
        setSupportActionBar(toolbar);

        jsonHandler = new JsonHandler();

        maskTextWatcher = new MaskTextWatcher(valorCotacao, maskFormatter);
        valorCotacao.addTextChangedListener(maskTextWatcher);

        if (!buscaCotacaoOnline()) {
            /*
             * Se algo der errado, o valor da TextView é passa a ser 0.00.
             */
            valorCotacao.setText( String.valueOf(0.00) );
        }

        final String[] ITEMS = getResources().getStringArray( R.array.array_estados );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapter);

        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Atualiza a TextView ICMS de acordo com o estado selecionado;
                 */
                String estado = spinnerEstados.getItemAtPosition(position).toString();

                int icms = jsonHandler.recuperaIcmsEstados(getApplicationContext(), estado);
                valorIcms.setText(String.valueOf(icms));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                valorIcms.setText( String.valueOf(0) );
            }
        });
        spinnerEstados.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                esconderTeclado(v);
                if(valorCotacao.hasFocus() || valorProdutoUsd.hasFocus() || valorFreteUsd.hasFocus()) {

                    if(getCurrentFocus() != null) {
                        getCurrentFocus().clearFocus();
                    }
                }
                return false;
            }
        });

        valorCotacao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(textInputLayoutCotacao.getError() != null) {

                    textInputLayoutCotacao.setError(null);
                    textInputLayoutCotacao.setErrorEnabled(false);
                }
            }
        });

        valorProdutoUsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(textInputLayoutProduto.getError() != null) {

                    textInputLayoutProduto.setError(null);
                    textInputLayoutProduto.setErrorEnabled(false);
                }

            }
        });


        btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (valorCotacao.getText().toString().equals("") || valorProdutoUsd.getText().toString().equals("")) {

                    if(valorCotacao.getText().toString().equals("")) {

                        textInputLayoutCotacao.setErrorEnabled(true);
                        textInputLayoutCotacao.setError("Campo obrigatório");
                    }

                    if (valorProdutoUsd.getText().toString().equals("")) {

                        textInputLayoutProduto.setErrorEnabled(true);
                        textInputLayoutProduto.setError("Campo obrigatório");
                    }

                    Snackbar.make(v, "Preencha todos os campos obrigatórios!", Snackbar.LENGTH_LONG).show();

                } else {
                    calculaValores();

                    if(getCurrentFocus() != null) {
                        getCurrentFocus().clearFocus();
                    }
                }

            }
        });

        btCalcular.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                esconderTeclado(v);
                return false;
            }
        });

        btLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpaValores();
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sobre_id:
                Intent intentSobre = new Intent(getApplicationContext(), SobreActivity.class);
                startActivity(intentSobre);
                return true;

            case R.id.menu_item_atualizar:
                View view = getCurrentFocus();
                if (view != null) {
                    esconderTeclado(view);
                }
                buscaCotacaoOnline();
                return true;

            case R.id.menu_item_ajuda:
                Intent intentAjuda = new Intent(getApplicationContext(), AjudaActivity.class);
                if(moeda != null) {
                    intentAjuda.putExtra("ultimaAtt", moeda.getUltimaConsulta());
                    intentAjuda.putExtra("fonte", moeda.getFonte());

                } else {
                    intentAjuda.putExtra("ultimaAtt", "Não foi possível obter a ultima atualização.");
                    intentAjuda.putExtra("fonte", "Verifique sua conexão e tente atualizar a cotação.");

                }

                startActivity(intentAjuda);
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

        if (!valorIcms.getText().toString().equals("")) {
            icmsEstado = Double.parseDouble(valorIcms.getText().toString()) / 100;
        }

        double freteBrl = freteUsd * valorCotacaoDolar;
        double produtoBrl = produtoUsd * valorCotacaoDolar;
        double importacao = (freteBrl + produtoBrl) * 0.6;
        double baseCalculo1 = produtoBrl + importacao + freteBrl;
        double baseCalculo2 = baseCalculo1 / (1 - icmsEstado);
        double icms = baseCalculo2 * icmsEstado;
        double total = baseCalculo1 + icms;

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(locale));

        String strProdutoBrl = "R$ " + decimalFormat.format(produtoBrl);
        String strFreteBrl = "R$ " + decimalFormat.format(freteBrl);
        String strImportacao = "R$ " + decimalFormat.format(importacao);
        String strIcms = "R$ " + decimalFormat.format(icms);
        String strTotal = "R$ " + decimalFormat.format(total);

        valorProdutoBrl.setText(strProdutoBrl);
        valorFreteBrl.setText(strFreteBrl);
        valorIcmsBrl.setText(strIcms);
        valorImportacaoBrl.setText(strImportacao);
        valorTotalBrl.setText(strTotal);

       /*
        * Animação de scroll (1 segundo) até a TextView valorTotalBrl.
        */
        ObjectAnimator.ofInt(scrollView, "ScrollY", valorTotalBrl.getBottom()).setDuration(1000).start();
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

                Snackbar.make(parentView , "Não foi possível atualizar a cotação online.", Snackbar.LENGTH_LONG).show();

                return false;
            }

    }

    /*
     * Fecha o teclado/spinner caso esteja aberto.
     */
    private void esconderTeclado(View v) {

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /*
     * Limpa o valor de todos os campos
     */
    private void limpaValores() {
        spinnerEstados.setSelection(0);
        valorCotacao.setText("");
        valorFreteUsd.setText("");
        valorProdutoUsd.setText("");
        valorProdutoBrl.setText("");
        valorFreteBrl.setText("");
        valorIcmsBrl.setText("");
        valorImportacaoBrl.setText("");
        valorTotalBrl.setText("");
    }

    /*
     * Metodo executado após AsyncTask concluir.
     * Se acontecer algum problema na hora de buscar a cotação, o valor da EditText passa a ser 0.00.
     */
    @Override
    public void onTaskComplete(String result) {

        moeda = jsonHandler.montarObjeto(result);

        if (moeda != null) {
            String valorMoeda = String.valueOf(moeda.getValor());
            valorCotacao.setText(valorMoeda);

            /*
             * Usar o .setText() não aciona o listener, portando, é necessário verificar se há erros.
             */
            if(textInputLayoutCotacao.getError() != null) {
                textInputLayoutCotacao.setError(null);
                textInputLayoutCotacao.setErrorEnabled(false);
            }

            Snackbar.make(parentView , "Valor da cotação foi atualizado!", Snackbar.LENGTH_LONG).show();


        } else {
            valorCotacao.setText( String.valueOf(0.00) );
        }

    }


}
