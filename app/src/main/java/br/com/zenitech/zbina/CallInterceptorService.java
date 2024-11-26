package br.com.zenitech.zbina;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class CallInterceptorService extends Service {

    private static final String CHANNEL_ID = "CallInterceptorChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "CallInterceptorService";
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        // Verificação de permissões SOMENTE no Android 14 (API 34)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permissão FOREGROUND_SERVICE_DATA_SYNC não concedida no Android 14 ou superior.");
                stopSelf(); // Encerrar o serviço se a permissão não foi concedida
                return;
            }
        }

        // Configurar o serviço em primeiro plano
        startForeground(NOTIFICATION_ID, createNotification());

        // Configurar o WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Zenitech::NotificationWakeLock");
            wakeLock.acquire(20 * 60 * 60 * 1000L); // Máximo de 20 horas
        }
        Log.d(TAG, "Serviço de interceptação de chamadas criado.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Serviço de interceptação de chamadas iniciado.");
        return START_STICKY; // Serviço reiniciado automaticamente se for encerrado
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
        Log.d(TAG, "Serviço de interceptação de chamadas encerrado.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Este serviço não suporta binding
        return null;
    }

    private Notification createNotification() {
        // Informações da notificação
        CharSequence name = "Interceptador de Chamadas";
        String description = "Serviço em execução.";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Criar o canal de notificação no Android 8+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Criar a notificação
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(name)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification) // Atualize com seu ícone de notificação
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }
}


