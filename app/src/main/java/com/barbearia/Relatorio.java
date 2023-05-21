package com.barbearia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class Relatorio extends AppCompatActivity {

    private TextView nomeBarbeiro, nomeCliente, horario, procedimento, valorTotal;
    private Button btnAgendamento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        Log.i("STATS: ", "RELATORIO");
        nomeBarbeiro = findViewById(R.id.nomebarbeiro_sv);
        nomeCliente  = findViewById(R.id.nomecliente_sv);
        horario  = findViewById(R.id.data_sv);
        procedimento  = findViewById(R.id.procedimento_sv);
        valorTotal  = findViewById(R.id.valor_sv);

        nomeBarbeiro.setText(getIntent().getStringExtra("nomeBarbeiro"));
        nomeCliente.setText(getIntent().getStringExtra("nomeCliente"));
        horario.setText(getIntent().getStringExtra("horario"));
        procedimento.setText(getIntent().getStringExtra("procedimento"));
        valorTotal.setText(getIntent().getStringExtra("valorTotal"));


        btnAgendamento = findViewById(R.id.btn_capturar);
        btnAgendamento.setOnClickListener(v -> {
            AlertDialog.Builder agendamentoRealizado = new AlertDialog.Builder(Relatorio.this);
            agendamentoRealizado.setMessage("Agendamento realizado com Sucesso! Sera necessario aprensetar o print do agendamento que se encontra na galeria no dia marcado");
            agendamentoRealizado.setCancelable(false);
            agendamentoRealizado.setPositiveButton("ok", (dialogInterface, i) -> {
                Intent intent = new Intent(this, MainActivity.class);
                captureScreen();
                startActivity(intent);
            });
            agendamentoRealizado.create().show();
        });

    }

    @Override
    public void onBackPressed() {
        // Lógica personalizada aqui
    }

    private void captureScreen() {
        // Captura a tela do aplicativo
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Salva a imagem na galeria
        String filename = "screenshot.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        ContentResolver resolver = getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
            Toast.makeText(this, "Captura de tela salva na galeria", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Falha ao salvar a captura de tela", Toast.LENGTH_SHORT).show();
        }
    }


}