package com.barbearia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgendarHorario extends AppCompatActivity {

    Spinner spinner;
    List<String> itens;
    private String cidade, itemSelecionado, idBarbeiro, mensagemAgendamento, tokenBarbeiro, nomeClienteUrl, procedimentoCliente = "%20";

    private ArrayList<String> procedimento = new ArrayList<>();
    private int valorCorte = 0;
    private static final String HOST = "http://192.168.0.107/";
    //private static final String HOST = "https://barbershopcco.000webhostapp.com/";

    private TextView valorTotal;
    private EditText nomeCliente;
    private CheckBox tesoura, maquina, barba, sobrancelha;
    Button myButton, agendar;

    private JsonObject nomeBarbeiroSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_horario);

        String urlImage = "https://vinicius-melo.github.io/fotos/";

        AlertDialog.Builder erroAgendamento = new AlertDialog.Builder(AgendarHorario.this);


        //VARIAVEIS VINDAS DO ADAPTER
        String nomeBarbeiro = getIntent().getStringExtra("nomeBarbeiro");
        String fotoBarbeiro = getIntent().getStringExtra("fotoBarbeiro");
        String cidadeBarbeiro = getIntent().getStringExtra("cidadeBarbeiro");

        Log.i("nomebarbeiro", "nome barbeiro horario: " + nomeBarbeiro);

        if (cidadeBarbeiro.equals("1")) {
            cidade = "RECIFE";
        } else if (cidadeBarbeiro.equals("2")) {
            cidade = "CARUARU";
        }

        carregarPerfil(urlImage, fotoBarbeiro);
        listaHorario(nomeBarbeiro);

        myButton = findViewById(R.id.btn_back);
        myButton.setOnClickListener(v -> {
            // chama a próxima activity
            Intent intent = new Intent(AgendarHorario.this, EscolhaBarbeiros.class);
            intent.putExtra("cidade", cidade);
            startActivity(intent);
        });

        maquina = findViewById(R.id.corte_maquina);
        tesoura = findViewById(R.id.corte_tesoura);
        barba = findViewById(R.id.corte_barba);
        sobrancelha = findViewById(R.id.corte_sobrancelha);
        valorTotal = findViewById(R.id.valorTotal);

        verificarChecked();

        agendar = findViewById(R.id.btn_agendar);
        agendar.setOnClickListener(v -> {

            nomeCliente = findViewById(R.id.txt_nome_cliente);
            nomeClienteUrl = nomeCliente.getText().toString();
            nomeClienteUrl = nomeClienteUrl.replace(" ", "%20");


            AlertDialog.Builder confirmaAgendar = new AlertDialog.Builder(AgendarHorario.this);
            confirmaAgendar.setTitle("Confirma o agendamento?");
            confirmaAgendar.setCancelable(false);

            AlertDialog.Builder agendamentoRealizado = new AlertDialog.Builder(AgendarHorario.this);
            agendamentoRealizado.setMessage("AGENDAMENTO REALIZADO COM SUCESSO");
            agendamentoRealizado.setCancelable(false);
            agendamentoRealizado.setPositiveButton("ok", (dialogInterface, i) -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });

            confirmaAgendar.setPositiveButton("CONFIRMAR", (dialogInterface, i) -> {
                if (!itemSelecionado.equals("Agende seu horario")) {
                    if (maquina.isChecked() || tesoura.isChecked() || sobrancelha.isChecked() || barba.isChecked()) {
                        if (!TextUtils.isEmpty(nomeCliente.getText())) {

                            listarProcedimentos();

                            mensagemAgendamento = "NOVO%20AGENDAMENTO:%0A|%0A|%0A|%0A|%0A|%20Cliente%20:%20" + nomeClienteUrl +
                                    "%0A|%20Horario:%20" + itemSelecionado + "%0A|%20Procedimentos:%20" + procedimentoCliente +
                                    "%0A%0A%0ATOTAL%20A%20PAGAR:%20R$%20" + valorCorte + ",00";
                            agendamentoTelegram();
                            agendamentoRealizado();


                            agendamentoRealizado.create().show();


                        } else {
                            erroAgendamento.setMessage("Por favor insira seu  nome");
                            erroAgendamento.show();
                        }
                    } else {
                        erroAgendamento.setMessage("Escolha o procedimento  para o seu agendamento");
                        erroAgendamento.show();
                    }
                } else {
                    erroAgendamento.setMessage("Escolha um horario para o seu agendamento");
                    erroAgendamento.show();
                }
            });
            confirmaAgendar.setNegativeButton("CORRIGIR AGENDAMENTO", null);
            confirmaAgendar.create().show();
        });
    }

    @SuppressLint("SetTextI18n")
    public void verificarChecked() {
        maquina.setOnCheckedChangeListener((compoundButton, b) -> {
            if (maquina.isChecked()) {
                if (tesoura.isChecked()) {
                    valorCorte += 15;
                } else {
                    tesoura.setChecked(true);
                    valorCorte += 30;
                }
                procedimento.add("MAQUINA");
                valorTotal.setText("R$ " + valorCorte + ",00");
            } else {
                if (tesoura.isChecked()) {
                    valorCorte -= 15;
                    procedimento.remove("MAQUINA");
                } else {
                    valorCorte -= 30;
                    procedimento.remove("MAQUINA");
                    procedimento.remove("TESOURA");
                }

                valorTotal.setText("R$ " + valorCorte + ",00");
                if (valorCorte <= 0) {
                    valorTotal.setText("");
                }
            }
        });

        tesoura.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tesoura.isChecked()) {
                if (!maquina.isChecked()) {
                    valorCorte += 15;
                    valorTotal.setText("R$ " + valorCorte + ",00");
                } else {
                    procedimento.add("TESOURA");
                }
            } else {
                if (!maquina.isChecked()) {
                    valorCorte -= 15;
                    procedimento.remove("TESOURA");
                }
                valorTotal.setText("R$ " + valorCorte + ",00");
                if (valorCorte <= 0) {
                    valorTotal.setText("");
                }
            }
        });

        barba.setOnCheckedChangeListener((compoundButton, b) -> {
            if (barba.isChecked()) {
                valorCorte += 30;
                valorTotal.setText("R$ " + valorCorte + ",00");
                procedimento.add("BARBA");
            } else {
                valorCorte -= 30;
                valorTotal.setText("R$ " + valorCorte + ",00");
                procedimento.remove("BARBA");
                if (valorCorte <= 0) {
                    valorTotal.setText("");
                }
            }
        });

        sobrancelha.setOnCheckedChangeListener((compoundButton, b) -> {
            if (sobrancelha.isChecked()) {
                valorCorte += 10;
                valorTotal.setText("R$ " + valorCorte + ",00");
                procedimento.add("SOBRANCELHA");
            } else {
                valorCorte -= 10;
                procedimento.remove("SOBRANCELHA");
            }
            valorTotal.setText("R$ " + valorCorte + ",00");
            if (valorCorte <= 0) {
                valorTotal.setText("");
            }
        });


    }

    public void listaHorario(String nomeBarbeiro) {

        spinner = findViewById(R.id.marcacao_data_horario);
        itens = new ArrayList<>();

        itens.add("Agende seu horario");

        String[] nome = nomeBarbeiro.split(" ");

        Log.i("tamanhoNome", "tamanho nome: " + nome.length);
        if (nome.length > 1) {
            String verificarBarbeiro = HOST + "barbershop/read.php?nome_barbeiro=" + nome[0] + "%20" + nome[1];

            Ion.with(AgendarHorario.this)
                    .load(verificarBarbeiro)
                    .asJsonArray()
                    .setCallback((e, result) -> {

                        Log.i("token", "URL: " + verificarBarbeiro);
                        Log.i("token", "E: " + e);
                        Log.i("token", "RESULT: " + result);
                        for (int i = 0; i < result.size(); i++) {
                            nomeBarbeiroSelecionado = result.get(i).getAsJsonObject();
                            idBarbeiro = nomeBarbeiroSelecionado.get("id").getAsString();
                            tokenBarbeiro = nomeBarbeiroSelecionado.get("token").getAsString();
                            String url = HOST + "barbershop/agendar_horario.php?id_barbeiro=" + nomeBarbeiroSelecionado.get("id").getAsString();

                            Log.i("token", "URL: " + url);
                            Ion.with(AgendarHorario.this).load(url).asJsonArray().setCallback((a, resultado) -> {
                                for (int g = 0; g < resultado.size(); g++) {
                                    JsonObject objeto = resultado.get(g).getAsJsonObject();
                                    itens.add(objeto.get("horario disponivel").getAsString());
                                }
                            });


                        }
                    });
        } else {
            String verificarBarbeiro = HOST + "/barbershop/read.php?nome_barbeiro=" + nomeBarbeiro;
            Ion.with(AgendarHorario.this).load(verificarBarbeiro).asJsonArray().setCallback((e, result) -> {
                for (int i = 0; i < result.size(); i++) {
                    nomeBarbeiroSelecionado = result.get(i).getAsJsonObject();
                    idBarbeiro = nomeBarbeiroSelecionado.get("id").getAsString();
                    tokenBarbeiro = nomeBarbeiroSelecionado.get("token").getAsString();
                    String url = HOST + "/barbershop/agendar_horario.php?id_barbeiro=" + nomeBarbeiroSelecionado.get("id").getAsString();
                    Ion.with(AgendarHorario.this).load(url).asJsonArray().setCallback((a, resultado) -> {
                        for (int g = 0; g < resultado.size(); g++) {
                            JsonObject objeto = resultado.get(g).getAsJsonObject();
                            itens.add(objeto.get("horario disponivel").getAsString());
                        }
                    });
                }
            });
        }


        spinner.setAdapter(new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, itens));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Aqui você pode capturar o valor do item selecionado
                itemSelecionado = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    public void carregarPerfil(String urlImage, String fotoBarbeiro) {
        ImageView fotoBarbeiroActivity = findViewById(R.id.perfil_barbeiro);
        Picasso.get().load(urlImage + fotoBarbeiro).into(fotoBarbeiroActivity);

    }

    public void agendamentoRealizado() {
        String url = HOST + "/barbershop/deletar.php?id_barbeiro=" + idBarbeiro + "&horario_disponivel=" + itemSelecionado;
        Ion.with(AgendarHorario.this)
                .load(url)
                .asString()
                .setCallback((e, result) -> {
                    if (e == null) {
                        Log.i("sucesso" + "sucesso", url);
                    }
                });
    }

    public void agendamentoTelegram() {
        String urlTelegram = HOST + "barbershop/bot_telegram/ola-mundo.php?chat_id=" + tokenBarbeiro + "&message=" + mensagemAgendamento;
        Ion.with(AgendarHorario.this)
                .load(urlTelegram)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e("TAG", "Erro ao enviar requisição: " + e.getMessage() + " " + urlTelegram);
                    } else {
                        Log.i("TAG", "Requisição enviada com sucesso! " + urlTelegram);
                    }
                });

    }

    public void listarProcedimentos() {
        for (int i = 0; i < procedimento.size(); i++) {
            Log.i("TAG", "procedimento: " + procedimentoCliente);
            procedimentoCliente += procedimento.get(i) + "%20";

        }
    }
}