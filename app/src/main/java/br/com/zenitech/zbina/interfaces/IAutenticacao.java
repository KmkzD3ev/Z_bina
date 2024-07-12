package br.com.zenitech.zbina.interfaces;


import br.com.zenitech.zbina.Models.DadosAutenticacao;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IAutenticacao {

    //RETORNA AS INFORMAÇÕES DA ENTREGA
    @FormUrlEncoded
    @POST("zbina_mobile.php")
    Call<DadosAutenticacao> Autenticar(
            @Field("opcao") String opcao,
            @Field("id_empresa") String id,
            @Field("unidade") String unidade,
            @Field("chave") String chave
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://zcall.com.br/gas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
