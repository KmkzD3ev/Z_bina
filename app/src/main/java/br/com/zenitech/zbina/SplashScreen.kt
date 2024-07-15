package br.com.zenitech.zbina
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import br.com.zenitech.zbina.Auth.Autenticacao
import br.com.zenitech.zbina.Prefs.Prefs

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private var prefs: Prefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefs = Prefs(this)

        contador()
    }

    private fun contador() {
        Handler(Looper.getMainLooper()).postDelayed({

            // SETA AS PREFERENCIA DE CONFIGURAÇÃO DO APP
            val intent: Intent
            if (prefs?.ativoApp == true) {
                intent = Intent(this, MainActivity::class.java)
            } else {
                intent = Intent(this, Autenticacao::class.java)
            }
            startActivity(intent)
        }, 2300)
    }
}