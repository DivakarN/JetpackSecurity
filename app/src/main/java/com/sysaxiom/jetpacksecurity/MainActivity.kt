package com.sysaxiom.jetpacksecurity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        writeFiles()
        readFiles()
        sharePrefData()
    }

    private fun readFiles(){
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val context = applicationContext
        val fileToRead = "my_sensitive_data.txt"

        val encryptedFile = EncryptedFile.Builder(
            File(filesDir, fileToRead),
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        val plaintext: ByteArray = byteArrayOutputStream.toByteArray()
        println("my_sensitive_data ${String(plaintext)}")
    }

    private fun writeFiles(){
        try{
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
            val context = applicationContext
            val fileToWrite = "my_sensitive_data.txt"

            val encryptedFile = EncryptedFile.Builder(
                File(filesDir, fileToWrite),
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val fileContent = "MY SUPER-SECRET INFORMATION"
                .toByteArray(StandardCharsets.UTF_8)
            encryptedFile.openFileOutput().apply {
                write(fileContent)
                flush()
                close()
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun sharePrefData(){
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val context = applicationContext
        val sharedPreferences = EncryptedSharedPreferences
            .create(
                "INTROSLIDER",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        val res = sharedPreferences.getBoolean("OnBoarding",true)
        println("my_sensitive_data $res")
        if(res){
            storeSharedPreference(sharedPreferences)
        }
        println("my_sensitive_data ${sharedPreferences.getBoolean("OnBoarding",false)}")
    }

    private fun storeSharedPreference(sharedPreferences : SharedPreferences){
        val sharedPrefsEditor = sharedPreferences.edit()
        sharedPrefsEditor.putBoolean("Onboarding", false)
        sharedPrefsEditor.apply()
    }
}
