package br.com.zenitech.zbina.Identificate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class IncomingCallReceiver extends BroadcastReceiver {

    // Declaração das constantes da URL
    private static final String BASE_URL = "https://zcall.com.br/zcall/chamada1.php";
    private static final String PARAM_ID_ZCALL = "idzcall=13";
    private static final String PARAM_UNIDADE = "unidade=3";
    private static final String PARAM_RAMAL = "ramal=000";

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (incomingNumber != null) {
                sendNumberToServer(incomingNumber);
                // Log imediatamente se o número estiver disponível
                Log.d("IncomingCallReceiver", "Chamada recebida de: " + incomingNumber);
            } else {
                // Adiciona um atraso para verificar novamente o número
                new Handler().postDelayed(() -> {
                    String delayedIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (delayedIncomingNumber != null) {
                        Log.d("IncomingCallReceiver", "Chamada recebida de: " + delayedIncomingNumber);
                        sendNumberToServer(delayedIncomingNumber);
                    } else {

                    }
                }, 1000); // 1 segundo de atraso
            }
        }
    }

    private void sendNumberToServer(String phoneNumber) {
        String fullUrl = BASE_URL + "?" + PARAM_ID_ZCALL + "&" + PARAM_UNIDADE + "&" + PARAM_RAMAL + "&numero=" + phoneNumber;
        Log.d("IncomingCallReceiver", "Enviando número para o servidor: " + fullUrl);

        new Thread(() -> {
            try {
                URL url = new URL(fullUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("GET");
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d("IncomingCallReceiver", "Número enviado com sucesso");
                    } else {
                        Log.d("IncomingCallReceiver", "Erro ao enviar número, código de resposta: " + responseCode);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("IncomingCallReceiver", "Erro ao enviar número para o servidor", e);
            }
        }).start();
    }
}



