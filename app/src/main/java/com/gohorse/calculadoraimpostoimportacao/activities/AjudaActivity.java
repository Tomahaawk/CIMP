package com.gohorse.calculadoraimpostoimportacao.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gohorse.calculadoraimpostoimportacao.R;

public class AjudaActivity extends AppCompatActivity {

    private TextView ultimaAtualizacao;
    private TextView fonte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        ultimaAtualizacao = (TextView) findViewById(R.id.tv_ultima_att_id);
        fonte = (TextView) findViewById(R.id.tv_fonte_id);

        Bundle extra = getIntent().getExtras();

        if(extra != null) {
            String ultimaAtt = extra.getString("ultimaAtt");
            String fonteCotacao = extra.getString("fonte");
            ultimaAtualizacao.setText(ultimaAtt);
            fonte.setText(fonteCotacao);
        }
    }

}
