package com.android.organize.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.organize.R;
import com.android.organize.models.Movimentacoes;

import java.util.List;

public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder>
{
    List<Movimentacoes> movimentacoes;
    Context context;

    public AdapterMovimentacao(List<Movimentacoes> movimentacoes, Context context)
    {
        this.movimentacoes = movimentacoes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Movimentacoes movimentacao = movimentacoes.get(position);

        holder.titulo.setText(movimentacao.getDescricao());
        holder.valor.setText(String.valueOf(movimentacao.getValor()));
        holder.categoria.setText(movimentacao.getCategoria());
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));

        if (movimentacao.getTipo().equals("Despesa"))
        {
            holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.valor.setText("-" + movimentacao.getValor());
        }
    }


    @Override
    public int getItemCount()
    {
        return movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView titulo, valor, categoria;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            titulo = itemView.findViewById(R.id.textAdapterTitulo);
            valor = itemView.findViewById(R.id.textAdapterValor);
            categoria = itemView.findViewById(R.id.textAdapterCategoria);
        }

    }

}
