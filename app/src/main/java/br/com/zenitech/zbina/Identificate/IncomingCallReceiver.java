package br.com.zenitech.zbina.Identificate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import br.com.zenitech.zbina.SecondPlane.SendNumberWorker;

public class IncomingCallReceiver extends BroadcastReceiver {
    /*
     * Interceptador de chamadas
     *
     * */
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE); // Pega o estado da chamada como extra
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            // Se o estado for igual a ringing (tocando)
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); // Passa o número para uma variável (incomingNumber)

            if (incomingNumber != null) { // Verifica se o número não é nulo
                scheduleSendNumber(context, incomingNumber); // Manda pra atividade de segundo plano pra envio do número
                // Log imediatamente se o número estiver disponível
                Log.d("IncomingCallReceiver", "Chamada recebida de: " + incomingNumber);
            } else {
                // Adiciona um atraso para verificar novamente o número
                new Handler().postDelayed(() -> {
                    String delayedIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (delayedIncomingNumber != null) {
                        Log.d("IncomingCallReceiver", "Chamada recebida de: " + delayedIncomingNumber);
                        scheduleSendNumber(context, delayedIncomingNumber);
                    }
                }, 1000); // 1 segundo de atraso
            }
        }
    }

    private void scheduleSendNumber(Context context, String phoneNumber) { // Passa o número para o worker que vai enviar via API
        if (phoneNumber.startsWith("0")) { // Verifica se o número começa com '0'
            phoneNumber = phoneNumber.substring(1); // Remove o primeiro '0'
        }

        if (phoneNumber.length() >= 2) { // Verifica se o número tem pelo menos 2 dígitos
            String ddd = phoneNumber.substring(0, 2); // Extrai os dois primeiros dígitos como DDD
            String remainingNumber = phoneNumber.substring(2); // Extrai o restante do número

            Data inputData = new Data.Builder()
                    .putString("dddNumero", ddd)
                    .putString("phoneNumber", remainingNumber)
                    .build();

            OneTimeWorkRequest sendNumberWorkRequest = new OneTimeWorkRequest.Builder(SendNumberWorker.class) // Inicializa o worker com o data
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(context).enqueue(sendNumberWorkRequest);
        } else {
            Log.e("IncomingCallReceiver", "Número de telefone inválido: " + phoneNumber);
        }
    }
}
