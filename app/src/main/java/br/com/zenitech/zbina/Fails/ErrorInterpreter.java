package br.com.zenitech.zbina.Fails;

import retrofit2.Response;

/**
 * CALSSE RESPONSAVEL POR INTERPRETAR ERROS TECNICOS DE FORMA MAIS AMIGAVEL PARA VISUALIZAÇAO
 */

public class ErrorInterpreter {

    public static String gerarMensagemAmigavel(Response<?> response, Throwable t, String respostaApi) {
        if (t != null) {
            // Erros técnicos de rede, SSL, timeout, etc.
            if (t instanceof java.net.UnknownHostException) {
                return "Sem conexão com a internet. Verifique sua rede.";
            } else if (t instanceof java.net.SocketTimeoutException) {
                return "Tempo de conexão esgotado. Tente novamente.";
            } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
                return "Falha de segurança na conexão. Verifique data/hora do aparelho.";
            } else if (t.getMessage() != null && t.getMessage().contains("403")) {
                return "Acesso negado. Verifique suas permissões.";
            } else {
                return "Erro de rede inesperado. Tente novamente.";
            }
        }

        if (response != null && !response.isSuccessful()) {
            int code = response.code();
            if (code == 500) {
                return "Erro interno no servidor. Aguarde e tente mais tarde.";
            } else if (code == 404) {
                return "Serviço não encontrado. Verifique a URL ou versão da API.";
            } else if (code == 401 || code == 403) {
                return "Permissão negada. Verifique sua autenticação.";
            } else {
                return "Erro na comunicação com o servidor. Código: " + code;
            }
        }

        if (respostaApi != null && respostaApi.contains("\"telefone\":\"ERRO\"")) {
            return "Falha ao registrar o número. Dados incompletos ou inválidos.";
        }

        return "Erro desconhecido. Contate o suporte se persistir.";
    }
}
