package br.com.zenitech.zbina;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import br.com.zenitech.zbina.Fails.Fail_Server;
import br.com.zenitech.zbina.Prefs.Prefs;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;



    // Instância de banco de dados
    private Prefs prefs;
    // Instância do elemento visual de exibição da chave do app
    private TextView txtChave;
    // Instância do alerta de encerramento
    private AlertDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnLogs = findViewById(R.id.btn_abrir_logs);
        btnLogs.setOnClickListener(v -> {
            Intent intent = new Intent(this, Fail_Server.class);
            startActivity(intent);
        });


        // Inicializa elementos da interface
        prefs = new Prefs(this);
        txtChave = findViewById(R.id.txtChave);

        // Exibe a chave armazenada nas preferências
        String chave = prefs.getChaveApp();
        if (chave != null) {
            txtChave.setText(chave);
        }

        // Verifica se as permissões já foram concedidas
        if (!hasNecessaryPermissions()) {
            requestNecessaryPermissions();
        } else {
            // Permissões já concedidas, inicia o serviço diretamente
            startCallInterceptorService();
        }
    }

    // Método para verificar se as permissões necessárias foram concedidas
    private boolean hasNecessaryPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    // Método para solicitar as permissões necessárias
    private void requestNecessaryPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
        }, PERMISSION_REQUEST_CODE);
    }

    // Trata o resultado da solicitação de permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasNecessaryPermissions()) {
                // Todas as permissões foram concedidas, iniciar o serviço
                startCallInterceptorService();
            } else {
                // Exibir alerta se as permissões foram negadas
                new AlertDialog.Builder(this)
                        .setTitle("Permissões Necessárias")
                        .setMessage("As permissões para acesso ao estado do telefone e registro de chamadas são necessárias para o funcionamento do aplicativo.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            // Encerrar ou redirecionar o usuário
                            finish();
                        })
                        .show();
            }
        }
    }

    // Método para iniciar o serviço de interceptação de chamadas
    private void startCallInterceptorService() {
        Intent serviceIntent = new Intent(this, CallInterceptorService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onBackPressed() {
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
        }

        exitDialog = new AlertDialog.Builder(this)
                .setMessage("Deseja realmente sair? O serviço de monitoramento de chamadas será encerrado.")
                .setCancelable(false)
                .setPositiveButton("Sim", (dialog, id) -> {
                    if (exitDialog != null && exitDialog.isShowing()) {
                        exitDialog.dismiss();
                    }
                    stopService(new Intent(this, CallInterceptorService.class));
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
        }
    }
}
