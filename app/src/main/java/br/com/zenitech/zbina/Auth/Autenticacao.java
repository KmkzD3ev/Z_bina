package br.com.zenitech.zbina.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.com.zenitech.zbina.MainActivity;
import br.com.zenitech.zbina.Models.DadosAutenticacao;
import br.com.zenitech.zbina.Prefs.Prefs;
import br.com.zenitech.zbina.R;

import br.com.zenitech.zbina.interfaces.IAutenticacao;
import br.com.zenitech.zcallbina.utils.CAux;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Autenticacao extends AppCompatActivity {

    Context context;
    EditText id, unidade;
    AppCompatButton btn_autenticacao;
    AlertDialog alerta;
    ProgressBar bProgress;
    LinearLayoutCompat llcFormAut;
    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        context = this;
        prefs = new Prefs(context);

        id = findViewById(R.id.id);
        unidade = findViewById(R.id.unidade);
        btn_autenticacao = findViewById(R.id.btn_autenticar);
        bProgress = findViewById(R.id.bProgress);
        llcFormAut = findViewById(R.id.llcFormAut);

        btn_autenticacao.setOnClickListener(v -> {
            String idCli = id.getText().toString().trim();
            String unidadeCli = unidade.getText().toString().trim();

            if (idCli.length() == 0 || unidadeCli.length() == 0) {
                showMsg("Preencha todos os campos!");
            } else {
                llcFormAut.setVisibility(View.GONE);
                bProgress.setVisibility(View.VISIBLE);
                String chave = new CAux().chave(idCli);
                validarDados(idCli, unidadeCli, chave);
            }
        });
    }

    private void validarDados(String id, String unidade, String chave) {


        Log.d("Autenticacao", "Enviando dados para a API:");
        Log.d("Autenticacao", "opcao: autenticar");
        Log.d("Autenticacao", "id_empresa: " + id);
        Log.d("Autenticacao", "unidade: " + unidade);
        Log.d("Autenticacao", "chave: " + chave);


        final IAutenticacao iAutenticacao = IAutenticacao.retrofit.create(IAutenticacao.class);

        final Call<DadosAutenticacao> call = iAutenticacao.Autenticar("autenticar", id, unidade, chave);

        call.enqueue(new Callback<DadosAutenticacao>() {
            @Override
            public void onResponse(@NonNull Call<DadosAutenticacao> call, @NonNull Response<DadosAutenticacao> response) {
                final DadosAutenticacao dados = response.body();
                Log.d("Autenticacao", "HTTP Status Code: " + response.code());

                if (dados != null && dados.id_unidade != null) {
                    if (!dados.id_unidade.equalsIgnoreCase("ERRO")) {

                        // SETA AS PREFERENCIA DE CONFIGURAÇÃO DO APP
                        prefs.setAtivoApp(true);
                        prefs.setChaveApp(chave);
                        prefs.setIdUnidade(dados.id_unidade);
                        prefs.setIdEmpresa(id);

                        showMsg(String.format("Id Unidade: %s", dados.id_unidade));

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        showMsg("Verifique seu dados e tente novamente");
                        llcFormAut.setVisibility(View.VISIBLE);
                        bProgress.setVisibility(View.GONE);
                    }
                } else {
                    showMsg("Verifique seu dados e tente novamente");
                    llcFormAut.setVisibility(View.VISIBLE);
                    bProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DadosAutenticacao> call, @NonNull Throwable t) {
                showMsg(String.format("ERRO: %s", t.getMessage()));
                Log.d("error", "onFailure: " + t.getMessage());;
                llcFormAut.setVisibility(View.VISIBLE);
                bProgress.setVisibility(View.GONE);
            }
        });
    }


    private void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void alertaCod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops,");
        String str = "Verifique suas informações e tente novamente!";
        builder.setMessage(str);
        builder.setPositiveButton("OK", (arg0, arg1) -> {});
        alerta = builder.create();
        alerta.show();
    }
}
