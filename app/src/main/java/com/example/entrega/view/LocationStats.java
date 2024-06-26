/**Basado en el código extraído de la siguiente libreria de GitHub
 Repositorio: https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartLib/src/main/java/com/github/mikephil/charting/charts/PieChart.java Autor: https://github.com/PhilJay
 Modificado por Alvaro Velasco para mostrar estadísticas de ubicación
 **/

package com.example.entrega.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Highlights;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.example.entrega.controller.DBHandler;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class LocationStats extends AppCompatActivity {
    private String id, name, type, latitude, longitude, altitude, date;

    // Variables de ejemplo (reemplaza con tus propios datos)
    private float[] horasPasadas = {0, 0, 0, 0, 0, 0, 0}; // Horas pasadas en la ubicación cada día
    private String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    TextView tvLocationStatus, tvLocationDate;
    private Button btnEditLocation;
    private ArrayList<ArrayList<String>> events;
    private String locationId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_stats);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        altitude = getIntent().getStringExtra("altitude");
        date = getIntent().getStringExtra("date");

        // Configurar el gráfico de pastel
        PieChart pieChart = findViewById(R.id.pieChart);

        tvLocationStatus = findViewById(R.id.idTVLocationStatus);
        btnEditLocation = findViewById(R.id.btnEditLocation);
        locationId = getIntent().getStringExtra("id");
        DBHandler dbHandler = new DBHandler(LocationStats.this.getApplicationContext());
        events = dbHandler.readEventsThisWeek(locationId);
        Iterator<ArrayList<String>> iter = events.iterator();
        LocalDateTime horaEntrada = null;
        while (iter.hasNext()) {
            ArrayList<String> act = iter.next();
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                System.out.println(act.get(2));
                System.out.println(act.get(3));

                if (act.get(3).equals("inside")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    horaEntrada = LocalDateTime.parse(act.get(2), formatter);
                } else {
                    if (horaEntrada != null) {

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime horaSalida = LocalDateTime.parse(act.get(2), formatter);
                        Integer dow = Integer.valueOf(String.valueOf(horaSalida.getDayOfWeek().getValue()));
                        Duration dur = Duration.between(horaEntrada, horaSalida);
                        Float diff = Float.valueOf(Long.toString(dur.getSeconds())) / 3600.0f;
                        horasPasadas[dow - 1] += diff;
                        System.out.println(horasPasadas[dow-1]);
                    }
                }
            }
            tvLocationStatus.setText("You have been " + act.get(3) + " since " + act.get(2));
        }
        setupPieChart(pieChart, horasPasadas);

        btnEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LocationStats.this, UpdateLocationActivity.class);

                i.putExtra("id", id);
                i.putExtra("name", name);
                i.putExtra("type", type);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("altitude", altitude);
                i.putExtra("date", date);
                startActivity(i);
            }
        });

    }

    private void setupPieChart(PieChart pieChart, float[] horasPasadas) {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < horasPasadas.length; i++) {
            entries.add(new PieEntry(horasPasadas[i], diasSemana[i]));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));
        PieDataSet dataSet = new PieDataSet(entries, "Horas pasadas en ubicación cada día");
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(15);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setCenterText("Horas esta semana");
        pieChart.setDrawEntryLabels(true);
        pieChart.setBackgroundColor(Color.parseColor("#4CAF50"));
    }
}
