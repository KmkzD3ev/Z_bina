package br.com.zenitech.zbina.Fails;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.zenitech.zbina.R;
import br.com.zenitech.zbina.adapter.FailLogAdapter;

public class Fail_Server extends AppCompatActivity {
    private RecyclerView logRecyclerView;
    private FailLogAdapter adapter;

    /**
     * ATIVIDADE RESPONSAVEL POR RENDERIZAR LOGS DE ERRROS QUANDO DISPONIVEIS
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fail_server);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logRecyclerView = findViewById(R.id.logList);
        TextView emptyView = findViewById(R.id.emptyView);
        ImageButton backButton = findViewById(R.id.backButton);

        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(v -> finish());

        SendLogRepository repository = new SendLogRepository(this);
        List<SendLogEntry> logs = repository.loadLogs();

        if (logs.isEmpty()) {
            logRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter = new FailLogAdapter(logs);
            logRecyclerView.setAdapter(adapter);
            emptyView.setVisibility(View.GONE);
            logRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}

