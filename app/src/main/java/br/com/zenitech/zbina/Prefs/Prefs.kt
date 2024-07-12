package br.com.zenitech.zbina.Prefs


import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

    var ativoApp: Boolean
        get() = prefs.getBoolean("ativo", false)
        set(value) = prefs.edit().putBoolean("ativo", value).apply()

    var chaveApp: String?
        get() = prefs.getString("chave", "")
        set(value) = prefs.edit().putString("chave", value).apply()

    var idUnidade: String?
        get() = prefs.getString("idUnidade", "")
        set(value) = prefs.edit().putString("idUnidade", value).apply()

    var idEmpresa: String?
        get() = prefs.getString("idEmpresa", "")
        set(value) = prefs.edit().putString("idEmpresa", value).apply()
}