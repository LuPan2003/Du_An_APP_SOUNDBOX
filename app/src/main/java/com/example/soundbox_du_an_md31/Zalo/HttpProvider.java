package com.example.soundbox_du_an_md31.Zalo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class HttpProvider {
    public static JSONObject sendPost(String URL, RequestBody formBody) {
        JSONObject data = new JSONObject();
        Log.d("bbb4", "đã vào ");
        try {
            Log.d("bbb5", "đã vào ");
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();
            Log.d("bbb6", "đã vào ");
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .callTimeout(5000, TimeUnit.MILLISECONDS)
                    .build();
            Log.d("bbb7", "đã vào ");
            Request request = new Request.Builder()
                    .url(URL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(formBody)
                    .build();
            Log.d("bbb8", "đã vào ");
            Response response = client.newCall(request).execute();
            Log.d("bbb9", "đã vào ");
            if (!response.isSuccessful()) {
                Log.println(Log.ERROR, "BAD_REQUEST", response.body().string());
                data = null;
            } else {
                data = new JSONObject(response.body().string());
            }

        }  catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return data;
    }
}