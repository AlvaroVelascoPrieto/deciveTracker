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
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RemoteDBHandler {
    static String ip = String.valueOf(R.string.ip);
    public static JSONObject checkUserCreds(String dni, String password) {
        try {
            String charset = "UTF-8";
            String query = String.format("?dni=%s&contrasena=%s",
                    URLEncoder.encode(dni, charset),
                    URLEncoder.encode(password, charset));
            URL url = new URL(String.format("http://34.70.109.203") + query);
            System.out.println(url);
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

    public static int registerUser(String dni, String password, String name, String lastName, String phone) {
        try {
            URL url = new URL(String.format("http://34.70.109.203"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String charset = "UTF-8";
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", charset);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);



            String query = String.format("dni=%s&" +
                            "contrasena=%s&" +
                            "nombre=%s&" +
                            "apellido=%s&" +
                            "telefono=%s&",
                    URLEncoder.encode(dni, charset),
                    URLEncoder.encode(password, charset),
                    URLEncoder.encode(name, charset),
                    URLEncoder.encode(lastName, charset),
                    URLEncoder.encode(phone, charset));

            OutputStream out = urlConnection.getOutputStream();
            out.write(query.getBytes());

            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String jsonString = rd.readLine();
            JSONObject jsonValue = new JSONObject(jsonString);

            int code = jsonValue.getInt("code");

            return code;
        } catch (Exception e) {
            return 500;
        }
    }


    }
