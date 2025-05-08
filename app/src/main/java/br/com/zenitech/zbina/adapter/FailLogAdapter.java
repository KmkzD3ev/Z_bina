package br.com.zenitech.zbina.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import br.com.zenitech.zbina.Fails.SendLogEntry;
import br.com.zenitech.zbina.R;

public class FailLogAdapter extends RecyclerView.Adapter<FailLogAdapter.LogViewHolder> {

    private final List<SendLogEntry> logList;

    public FailLogAdapter(List<SendLogEntry> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_send_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        SendLogEntry entry = logList.get(position);
        holder.timestamp.setText(entry.timestamp);
        holder.status.setText("Status: " + entry.status);
        holder.ddd.setText("DDD: " + entry.ddd);
        holder.numero.setText("Número: " + entry.numero);
        holder.resposta.setText("Resposta: " + (entry.resposta != null ? entry.resposta : "-"));
        holder.erro.setText("Erro: " + (entry.erro != null ? entry.erro : "-"));
    }

    @Override
    public int getItemCount() {
        return logList != null ? logList.size() : 0;
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp, status, ddd, numero, resposta, erro;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.log_timestamp);
            status = itemView.findViewById(R.id.log_status);
            ddd = itemView.findViewById(R.id.log_ddd);
            numero = itemView.findViewById(R.id.log_numero);
            resposta = itemView.findViewById(R.id.log_resposta);
            erro = itemView.findViewById(R.id.log_erro);
        }
    }
}
