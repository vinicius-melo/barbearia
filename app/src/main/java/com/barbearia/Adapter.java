package com.barbearia;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private final Context ctx;
    private final List<Barbeiros>  listBarbeiros;
    String urlImage = "https://vinicius-melo.github.io/fotos/";

    public Adapter (Context ctx2, List<Barbeiros> list){
        ctx = ctx2;
        listBarbeiros = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Barbeiros barbeiros = listBarbeiros.get(position);
        Picasso.get().load(urlImage+barbeiros.getFoto()).into(holder.fotoBarbeiro);
        holder.nomeBarbeiro.setText(barbeiros.getNome());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, AgendarHorario.class);
            intent.putExtra("nomeBarbeiro", barbeiros.getNome());
            intent.putExtra("fotoBarbeiro", barbeiros.getFoto());
            intent.putExtra("cidadeBarbeiro", barbeiros.getCidade());
            ctx.startActivity(intent);

            Log.i("nomebarbeiro", "nome barbeiro: "+barbeiros.getNome());
        });
    }

    @Override
    public int getItemCount() {
        return listBarbeiros.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nomeBarbeiro;
        ImageView fotoBarbeiro;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeBarbeiro = itemView.findViewById(R.id.nome_barbeiro);
            fotoBarbeiro = itemView.findViewById(R.id.foto_barbeiro);

        }
    }
}
