package com.android.organize.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.organize.R;
import com.android.organize.configs.ConfiguracaoFirebase;
import com.android.organize.helper.Base64Custom;
import com.android.organize.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity
{
    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");

        campoNome = findViewById(R.id.editTextNome);
        campoEmail = findViewById(R.id.editTextEmail);
        campoSenha = findViewById(R.id.editTextSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                campoNome.requestFocus();
                campoEmail.requestFocus();
                campoSenha.requestFocus();

                boolean validar = true;

                if (campoNome.getText().toString().length() == 0) {
                    campoNome.setError("Campo Nome obrigatório!");
                    campoNome.requestFocus();
                    validar = false;
                } else if (campoEmail.getText().toString().length() == 0) {
                    campoEmail.setError("Campo Email obrigatório!");
                    campoEmail.requestFocus();
                    validar = false;
                } else if (campoSenha.getText().toString().length() == 0) {
                    campoSenha.setError("Campo Senha obrigatório!");
                    campoSenha.requestFocus();
                    validar = false;
                } else if (validar) {
                    usuario = new Usuario();
                    usuario.setNome(campoNome.getText().toString());
                    usuario.setEmail(campoEmail.getText().toString());
                    usuario.setSenha(campoSenha.getText().toString());
                    cadastrarUsuario();
                }
            }
        });
    }

    public void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword
                (
                        usuario.getEmail(), usuario.getSenha()

                ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String idUsuario  = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();
                }
                else
                    {
                    String excecao = "";
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e)
                    {
                        excecao = "Insira uma senha contendo letras e números";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "Por favor, insira um e-mail válido!";
                    }
                    catch (FirebaseAuthUserCollisionException e)
                    {
                        excecao = "Esse email já foi cadastrado, favor inserir outro e-mail!";
                    }
                    catch (Exception e)
                    {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
