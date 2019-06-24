package com.android.organize.activitys;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.organize.R;
import com.android.organize.configs.ConfiguracaoFirebase;
import com.android.organize.helper.Base64Custom;
import com.android.organize.helper.DateCustomUtil;
import com.android.organize.models.Movimentacoes;
import com.android.organize.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity
{

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacoes movimentacoes = new Movimentacoes();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaRecuperada, despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editTextDataDespesa);
        campoCategoria = findViewById(R.id.editTextCategoriaDespesa);
        campoDescricao = findViewById(R.id.editTextDescricaoDespesa);
        campoValor = findViewById(R.id.editTextValorDespesa);

        //Retorna a data atual do sistema
        campoData.setText(DateCustomUtil.getDataAtual());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        recuperarDespesa();
    }

    public void salvarDespesa(View view)
    {

        if (validarCampos())
        {

            String date = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacoes.setValor(valorRecuperado);
            movimentacoes.setCategoria(campoCategoria.getText().toString());
            movimentacoes.setDescricao(campoDescricao.getText().toString());
            movimentacoes.setData(campoData.getText().toString());
            movimentacoes.setTipo("Despesa");

            despesaAtualizada = despesaRecuperada + valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacoes.salvarDatabase(date);
            Toast.makeText(this, "Despesa salva com Suvesso!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
            finish();
        }

    }

    public void atualizarDespesa(double despesa)
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }

    public Boolean validarCampos() {

        String txtValor = campoValor.getText().toString();
        String txtData = campoData.getText().toString();
        String txtCategoria = campoCategoria.getText().toString();
        String txtDescricao = campoDescricao.getText().toString();

        if (txtValor.isEmpty())
        {
            campoValor.setError("Insira o valor da despesa!");
            return false;
        }
        else if (txtData.isEmpty())
        {
            campoData.setError("Insira uma data!");
            return false;
        }
        else if(txtCategoria.isEmpty())
        {
            campoCategoria.setError("Insira a categoria da despesa!");
            return false;
        }
        else if(txtDescricao.isEmpty())
        {
            campoDescricao.setError("Insira a descrição da despesa!");
            return false;
        }
        else
        {
            return true;
        }
    }

    public void recuperarDespesa()
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaRecuperada = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), "Erro ao recuperar despesa do Banco de Dados: "+databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }
}
