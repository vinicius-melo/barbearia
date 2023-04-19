package com.barbearia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButtonCaruaru = findViewById(R.id.btn_locale_caruaru);
        myButtonCaruaru.setOnClickListener(v -> {
            // aqui você pode obter o texto do botão
            String buttonTextCaruaru = myButtonCaruaru.getText().toString();

            // chama a próxima activity
            Intent intent = new Intent(this, EscolhaBarbeiros.class);
            intent.putExtra("cidade", buttonTextCaruaru);
            startActivity(intent);
        });

        Button myButtonRecife = findViewById(R.id.btn_locale_recife);
        myButtonRecife.setOnClickListener(v -> {
            // aqui você pode obter o texto do botão
            String buttonTextRecife = myButtonRecife.getText().toString();
            Log.i("teste", "teste: " + buttonTextRecife);
            // chama a próxima activity
            Intent intent = new Intent(MainActivity.this, EscolhaBarbeiros.class);
            intent.putExtra("cidade", buttonTextRecife);
            startActivity(intent);
        });

    }
}