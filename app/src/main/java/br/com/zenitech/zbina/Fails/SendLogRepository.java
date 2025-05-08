package br.com.zenitech.zbina.Fails;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * CLASSE RESPONSAVEL PELA FORMATAÇAO E ENVIO DOS LOGS DE ERRROS
 *
 */

public class SendLogRepository {
    private static final String FILE_NAME = "send_logs.json";
    private final File logFile;
    private final Gson gson;

    public SendLogRepository(Context context) {
        logFile = new File(context.getFilesDir(), FILE_NAME);
        gson = new Gson();
    }

    public synchronized void saveLog(SendLogEntry entry) {
        List<SendLogEntry> logs = loadLogs();
        logs.add(0, entry);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            gson.toJson(logs, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//CARREGA  OS LOGS DE ERROS COM DATA E FORMATOS DO ITEM DE LISTAGEM
    public synchronized List<SendLogEntry> loadLogs() {
        if (!logFile.exists()) return new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            Type listType = new TypeToken<List<SendLogEntry>>(){}.getType();
            List<SendLogEntry> allLogs = gson.fromJson(reader, listType);

            List<SendLogEntry> validLogs = new ArrayList<>();
            long now = System.currentTimeMillis();
            long limit = now - (24 * 60 * 60 * 1000); // 24 horas

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));

            for (SendLogEntry entry : allLogs) {
                try {
                    Date logDate = sdf.parse(entry.timestamp);
                    if (logDate != null && logDate.getTime() >= limit) {
                        validLogs.add(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Ignora se a data estiver mal formatada
                }
            }

            // Sobrescreve o arquivo com apenas os válidos
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                gson.toJson(validLogs, writer);
            }

            return validLogs;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public synchronized void clearLogs() {
        if (logFile.exists()) logFile.delete();
    }
}
