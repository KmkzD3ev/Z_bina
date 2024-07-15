package br.com.zenitech.zbina.SecondPlane;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import br.com.zenitech.zbina.Prefs.Prefs;
import br.com.zenitech.zbina.interfaces.IDados;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SendNumberWorker extends Worker {
    private static final String TAG = "SendNumberWorker";
    private Prefs prefs;

    public SendNumberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        prefs = new Prefs(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Obtém o número de telefone dos dados de entrada
        String phoneNumber = getInputData().getString("phoneNumber");

        // Verifica se o número de telefone não é nulo
        if (phoneNumber != null) {
            // Envia o número de telefone para o servidor usando Retrofit
            sendNumberToServer(phoneNumber);

            // Retorna sucesso se o número foi enviado
            return Result.success();
        } else {
            // Retorna falha se o número de telefone for nulo
            return Result.failure();
        }
    }

    private void sendNumberToServer(String phoneNumber) {
        // Configura o interceptor de logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configura o cliente OkHttp com timeouts e o interceptor de logging
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .build();

        // Configura Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://zcall.com.br/gas/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        IDados apiService = retrofit.create(IDados.class);
        Call<String> call = apiService.enviarNumero("bina", "", "", "", "", phoneNumber, "", prefs.getIdUnidade(), prefs.getIdEmpresa(), prefs.getChaveApp());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Log das informações enviadas e URL chamada
                Log.d(TAG, "URL chamada: " + call.request().url());
                Log.d(TAG, "Dados enviados: opcao=bina, aparelho=, linha=, categoria=, ddd_numero=, numero=" + phoneNumber + ", id_chamada=, id_unidade=" + prefs.getIdUnidade() + ", id_empresa=" + prefs.getIdEmpresa() + ", chave=" + prefs.getChaveApp());

                // Verifica se a requisição foi feita com sucesso
                if (response.isSuccessful()) {
                    Log.d(TAG, "Número enviado com sucesso");
                    String responseBody = response.body();
                    Log.d(TAG, "Corpo da resposta: " + responseBody);
                } else {
                    Log.d(TAG, "Erro ao enviar número, código de resposta: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.d(TAG, "Erro na resposta: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler o corpo do erro", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Erro ao enviar número para o servidor", t);
            }
        });
    }
}
