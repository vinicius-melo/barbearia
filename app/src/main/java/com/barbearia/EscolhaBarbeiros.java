package com.barbearia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class EscolhaBarbeiros extends AppCompatActivity {
    private static final String HOST = "https://willbarbershop.net/";

   // private static final String HOST = "http://192.168.0.107/";
    private String cidade;
    List<Barbeiros> barbeirosList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_barbeiros);
        String cidadeBarbeiro = getIntent().getStringExtra("cidade");

        if ("RECIFE".equals(cidadeBarbeiro)) {
            cidade = "1";
        } else if ("CARUARU".equals(cidadeBarbeiro)) {
            cidade = "2";
        }

        //-----------------------------------------------------------------------
        barbeirosList = new ArrayList<>();
        adapter = new Adapter(EscolhaBarbeiros.this, barbeirosList);
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);

        //configurar o reclycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        listarBarbeiros(cidade);
        Button myButton = findViewById(R.id.btn_back);
        myButton.setOnClickListener(v -> {
            // chama a próxima activity
            Intent intent = new Intent(EscolhaBarbeiros.this, MainActivity.class);
            startActivity(intent);
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void listarBarbeiros(String cidade) {
        String url = HOST + "barbershop/read.php?cidade=" + cidade;
        Ion.with(EscolhaBarbeiros.this)
                .load(url)
                .asJsonArray()
                .setCallback((e, result) -> {
                    Log.i("result", "url: " + url);
                    Log.i("result", "e: " + e);
                    Log.i("result", "result: " + result);

                    for (int i = 0; i < result.size(); i++) {
                        JsonObject obj = result.get(i).getAsJsonObject();

                        Barbeiros b = new Barbeiros();
                        b.setNome(obj.get("nome").getAsString());
                        b.setFoto(obj.get("foto").getAsString());
                        b.setCidade(obj.get("cidade").getAsString());
                        barbeirosList.add(b);


                    }
                    adapter.notifyDataSetChanged();
                });

    }



}