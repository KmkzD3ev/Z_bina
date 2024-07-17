package br.com.zenitech.zbina;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.zenitech.zbina.Prefs.Prefs;

public class MainActivity extends AppCompatActivity {
    //Instancia de banco de dados
    private Prefs prefs;
    //Instancia do elemento visual de exibiçao da chave do app
    private TextView txtChave;
    //Instancia do alerta de encerramento
    private AlertDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicia o serviço de interceptação de chamadas em primeiro plano
        Intent serviceIntent = new Intent(this, CallInterceptorService.class);
        ContextCompat.startForegroundService(this, serviceIntent);


        // Inicializa o Prefs para gerenciar as preferências do aplicativo
        prefs = new Prefs(this);
        // Obtém a referência do TextView do layout
        txtChave = findViewById(R.id.txtChave);
        // Obtém a chave armazenada nas preferências e a exibe no TextView
        String chave = prefs.getChaveApp();
        if (chave != null) {
            txtChave.setText(chave);
        }

        // Verifica e solicita permissões necessárias para o funcionamento do aplicativo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
            }, 1);
        }
    }

    // Verifica o resultado da solicitação de permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                // Permissão negada
            }
        }
    }

    @Override
    public void onBackPressed() {

        // Verifica se o diálogo de saída está visível e o descarta se necessário
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
        }
        // Cria e exibe um diálogo de confirmação de saída
        exitDialog = new AlertDialog.Builder(this)
                .setMessage("Deseja realmente sair? O serviço de monitoramento de chamadas será encerrado.")
                .setCancelable(false)
                .setPositiveButton("Sim", (dialog, id) -> {
                    if (exitDialog != null && exitDialog.isShowing()) {
                        exitDialog.dismiss();
                        // Descartar o diálogo se estiver visível
                    }
                    // Para o serviço de interceptação de chamadas
                    stopService(new Intent(this, CallInterceptorService.class));
                    finishAffinity(); // Encerra todas as atividades
                    System.exit(0); // Encerra o processo
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    protected void onPause() {
        // Verifica se o diálogo de saída está visível e o descarta para evitar vazamentos de memória
        super.onPause();
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
        }
    }
}
