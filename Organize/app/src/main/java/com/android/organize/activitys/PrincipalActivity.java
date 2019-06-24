package com.android.organize.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.organize.R;
import com.android.organize.adapter.AdapterMovimentacao;
import com.android.organize.configs.ConfiguracaoFirebase;
import com.android.organize.helper.Base64Custom;
import com.android.organize.models.Movimentacoes;
import com.android.organize.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity
{
    private MaterialCalendarView calendarView;
    private TextView txtSaudacao, txtSaldo;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerMovimentacoes;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacoes> movimentacoes = new ArrayList<>();
    private Movimentacoes movimentacao;
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;

    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoTotal = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Organizze");
        setSupportActionBar(toolbar);

        instanciarObjetos();
        configuraCalendarView();
        swipe();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        recuperarDados();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUser);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }

    public void swipe()
    {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback()
        {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder)
            {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Excluir Movimentação da Conta");
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir essa movimentação da sua conta?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position);

                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsuario);

                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAnoSelecionado);

                movimentacaoRef.child(movimentacao.getKeyFirebase()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog  alert = alertDialog.create();
        alert.show();
    }

    public void atualizarSaldo()
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        if (movimentacao.getTipo().equals("Receita"))
        {
            receitaTotal = receitaTotal - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }
        if (movimentacao.getTipo().equals("Despesa"))
        {
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }

    }

    public void recuperarMovimentacoes()
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child(idUsuario)
                                     .child(mesAnoSelecionado);

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                movimentacoes.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren())
                {
                    Movimentacoes movimentacao = dados.getValue(Movimentacoes.class);

                    movimentacao.setKeyFirebase(dados.getKey());

                    movimentacoes.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), "Erro ao recuperar histórico de movimentações do Banco de Dados: "+databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void instanciarObjetos()
    {
        txtSaudacao = findViewById(R.id.textViewSaudacao);
        txtSaldo = findViewById(R.id.textViewSaldo);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerMovimentos);

        //Configurando Adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        //Configurando Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);
    }

    public void recuperarDados()
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        valueEventListenerUser = usuarioRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoTotal = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoTotal);

                txtSaudacao.setText("Olá, "+usuario.getNome());
                txtSaldo.setText("R$ "+ resultadoFormatado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), "Erro ao recuperar dados do Banco de Dados: "+databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuSair :
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void configuraCalendarView()
    {
        calendarView.state().edit()
                .setMaximumDate(CalendarDay.from(2030, 12, 31))
                .commit();

        CharSequence meses[] = {"Janeiro","Fevereiro", "Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths( meses );

        CharSequence semanas[] = {"Seg", "Ter","Quarta","Quinta","Sexta","Sáb","Domingo"};
        calendarView.setWeekDayLabels(semanas);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        mesAnoSelecionado = String.valueOf(mesSelecionado +"" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener()
        {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date)
            {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentacoes();
            }
        });
    }

    public void adicionarReceita(View view)
    {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionarDespesa(View view)
    {
        startActivity(new Intent(this, DespesasActivity.class));
    }
}
