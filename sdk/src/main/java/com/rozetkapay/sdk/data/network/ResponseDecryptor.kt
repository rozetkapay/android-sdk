package com.rozetkapay.sdk.data.network

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

interface ResponseDecryptor {
    fun decrypt(secret: String, data: String): String
}

class ResponseDecryptorImpl : ResponseDecryptor {
    override fun decrypt(
        secret: String,
        data: String,
    ): String {
        val (base64Iv, base64Cryptogram) = data.split(":")
        val iv = Base64.decode(base64Iv, Base64.DEFAULT)
        val cryptogram = Base64.decode(base64Cryptogram, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val secretKeySpec = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val decryptedBytes = cipher.doFinal(cryptogram)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}