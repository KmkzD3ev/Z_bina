package br.com.zenitech.zbina.Fails;

public class SendLogEntry {
    public String timestamp;
    public String status;
    public String url;
    public String ddd;
    public String numero;
    public String resposta;
    public String erro;

    public SendLogEntry(String timestamp, String status, String url, String ddd, String numero, String resposta, String erro) {
        this.timestamp = timestamp;
        this.status = status;
        this.url = url;
        this.ddd = ddd;
        this.numero = numero;
        this.resposta = resposta;
        this.erro = erro;
    }
}
