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
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);                       //pega o estado da chamada como extra
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            //se o estado for igual a ringing (tocando
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); //passa o número para uma variável (icomingNumber)

            if (incomingNumber != null) {   //verifica se o número não é nulo

                scheduleSendNumber(context, incomingNumber);                                       //manda pra atividade de segundoo plano pra  envio do número
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

    private void scheduleSendNumber(Context context, String phoneNumber) { //passa o número para o worker que vai enviar via Api
        Data inputData = new Data.Builder()
                .putString("phoneNumber", phoneNumber)
                .build();

        OneTimeWorkRequest sendNumberWorkRequest = new OneTimeWorkRequest.Builder(SendNumberWorker.class) // iniciliza o worker com o data
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(sendNumberWorkRequest);
    }
}
