package com.barbearia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity(); // Fecha todas as atividades da pilha, incluindo a atividade atual
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Pressione novamente para sair do aplicativo", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // Define um tempo de espera de 2 segundos
    }

}