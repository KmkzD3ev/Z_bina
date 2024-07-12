package br.com.zenitech.zcallbina.utils

import kotlin.random.Random

class CAux {

    fun chave(id: String): String {
        val mChave = StringBuilder()
        mChave.append(this.zerosAEsquerda(id) + "-")

        val mRandom = List(12) { Random.nextInt(0, 9) }
        for (num in mRandom) {
            mChave.append(num.toString())
        }
        return mChave.insert(mChave.length - 6, "-").toString()
    }

    fun zerosAEsquerda(numero: String): String {
        return String.format("%06d", numero.toInt())
    }

    fun soNumeros(txt: String): String {
        return txt.replace("[^0-9]*".toRegex(), "")
    }
}