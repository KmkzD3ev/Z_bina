package br.com.zenitech.zbina.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IDados {

    @FormUrlEncoded
    @POST("zbina_mobile.php")
    Call<String> enviarNumero(
            @Field("opcao") String opcao,
            @Field("aparelho") String aparelho,
            @Field("linha") String linha,
            @Field("categoria") String categoria,
            @Field("ddd_numero") String dddNumero,
            @Field("numero") String numero,
            @Field("id_chamada") String idChamada,
            @Field("id_unidade") String idunidade,
            @Field("id_empresa") String idEmpresa,
            @Field("chave") String chave
    );
}
