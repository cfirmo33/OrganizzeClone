package com.android.organize.activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.organize.R;
import com.android.organize.configs.ConfiguracaoFirebase;
import com.android.organize.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity
{
    private EditText campoEmail, campoSenha;
    private Button botaoAcessar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editTextEmailLogin);
        campoSenha = findViewById(R.id.editTextSenhaLogin);
        botaoAcessar = findViewById(R.id.buttonAcessar);

        botaoAcessar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                campoEmail.requestFocus();
                campoSenha.requestFocus();

                boolean validar = true;

                if (campoEmail.getText().toString().length() == 0)
                {
                    campoEmail.setError("Campo Email obrigatório!");
                    campoEmail.requestFocus();
                    validar = false;
                }
                else if (campoSenha.getText().toString().length() == 0)
                {
                    campoSenha.setError("Campo Senha obrigatório!");
                    campoSenha.requestFocus();
                    validar = false;
                }
                else
                {
                    usuario = new Usuario();
                    usuario.setEmail(campoEmail.getText().toString());
                    usuario.setSenha(campoSenha.getText().toString());
                    validarLogin();
                }
            }
        });
    }

    public void validarLogin()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword
        (
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    abrirTelaPrincipal();
                }
                else
                {
                    String excecao = "";
                    try
                    {
                        throw task.getException();
                    }
                    catch(FirebaseAuthInvalidUserException e)
                    {
                        excecao = "Usuário não encontrado!\n Verifique seus dados e tente novamente";
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "E-mail ou Senha não encontrados";
                    }
                    catch (Exception e)
                    {
                        excecao = "Erro ao realizar Login: " +e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal()
    {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}
