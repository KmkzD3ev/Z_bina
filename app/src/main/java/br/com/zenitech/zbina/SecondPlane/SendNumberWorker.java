package br.com.zenitech.zbina.SecondPlane;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendNumberWorker extends Worker {
    /*
    * Classe responsável por enviar o número para o servidor
    *
    * Monitor de chamadas
    *
    *
    * */

    private static final String BASE_URL = "https://zcall.com.br/zcall/chamada1.php"; //url do servidor
    private static final String PARAM_ID_ZCALL = "idzcall=13"; //parametro
    private static final String PARAM_UNIDADE = "unidade=3"; //paramentro
    private static final String PARAM_RAMAL = "ramal=000"; //parametro

    public SendNumberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {  // Constutor da classe
        super(context, workerParams);
    }

    @NonNull
    @Override

    /*Inicia o processo  EM SEGUNDO PLANO
    *
    * */
    public Result doWork() {
        // Obtém o número de telefone dos dados de entrada
        String phoneNumber = getInputData().getString("phoneNumber");

        // Verifica se o número de telefone não é nulo
        if (phoneNumber != null) {
            // Envia o número de telefone para o servidor
            sendNumberToServer(phoneNumber);

            // Retorna sucesso se o número foi enviado
            return Result.success();
        } else {
            // Retorna falha se o número de telefone for nulo
            return Result.failure();
        }
    }
    private void sendNumberToServer(String phoneNumber) { // Método responsável por enviar o número para o servidor
        String fullUrl = BASE_URL + "?" + PARAM_ID_ZCALL + "&" + PARAM_UNIDADE + "&" + PARAM_RAMAL + "&numero=" + phoneNumber;
        Log.d("SendNumberWorker", "Enviando número para o servidor: " + fullUrl); //Construtor da URL

        try {
            URL url = new URL(fullUrl);  //Trata o Sucess
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("SendNumberWorker", "Número enviado com sucesso");
                } else {
                    Log.d("SendNumberWorker", "Erro ao enviar número, código de resposta: " + responseCode);
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) { //Trata o Error
            Log.e("SendNumberWorker", "Erro ao enviar número para o servidor", e);
        }
    }
}