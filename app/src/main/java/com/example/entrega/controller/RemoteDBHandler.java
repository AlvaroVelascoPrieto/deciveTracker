package com.example.entrega.controller;

import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.entrega.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RemoteDBHandler {
    static String ip = String.valueOf(R.string.ip);
    public static JSONObject checkUserCreds( String dni, String contrasena) {
        try {
            String charset = "UTF-8";

            String query = String.format("?dni=%s&contrasena=%s",
                    URLEncoder.encode(dni, charset),
                    URLEncoder.encode(contrasena, charset));

            URL url = new URL(String.format("http://34.70.109.203", ip, query));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Charset", charset);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));

            String jsonResponse = rd.readLine();

            JSONObject jsonValue = new JSONObject(jsonResponse);

            int code = jsonValue.getInt("code");

            if (code == 200) {
                return jsonValue;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error on sign up. Ocurrió un error al intentar iniciar sesión.");
            System.out.println(e.toString());
            return null;
        }



    }
}
