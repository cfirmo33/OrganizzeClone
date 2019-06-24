package com.android.organize.models;

import com.android.organize.configs.ConfiguracaoFirebase;
import com.android.organize.helper.Base64Custom;
import com.android.organize.helper.DateCustomUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacoes
{

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private String keyFirebase;
    private double valor;
    private FirebaseAuth autenticacao;

    public Movimentacoes()
    {

    }

    public void salvarDatabase(String dateEscolhida)
    {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUser = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        String mesAno = DateCustomUtil.mesAnoDataEscolhida(dateEscolhida);
        //String mesAno = DateCustom.mesAnoDataEscolhida( dataEscolhida );
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("movimentacao")
                .child(idUser)
                .child(mesAno)
                .push()
                .setValue(this);
    }

    public String getKeyFirebase()
    {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase)
    {
        this.keyFirebase = keyFirebase;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
