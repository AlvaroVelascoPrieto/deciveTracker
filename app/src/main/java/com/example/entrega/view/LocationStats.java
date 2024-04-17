package com.example.entrega.view;

import android.os.Bundle;
import android.text.Highlights;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LocationStats extends AppCompatActivity {

    // Variables de ejemplo (reemplaza con tus propios datos)
    private float[] horasPasadas = {7.5f, 8.0f, 7.8f, 8.2f, 7.7f, 1.0f, 2.2f}; // Horas pasadas en la ubicación cada día
    private String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    TextView tvLocationStatus, tvLocationDate;
    private ArrayList<ArrayList<String>> events;
    private String locationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_stats);

        // Configurar el gráfico de pastel
        PieChart pieChart = findViewById(R.id.pieChart);
        setupPieChart(pieChart, horasPasadas);

        tvLocationStatus = findViewById(R.id.idTVLocationStatus);
        tvLocationDate = findViewById(R.id.idTVLocationDate);
        locationId = getIntent().getStringExtra("id");
        DBHandler dbHandler = new DBHandler(LocationStats.this.getApplicationContext());
        events = dbHandler.readEventsThisWeek(locationId);
        Iterator<ArrayList<String>> iter = events.iterator();
        tvLocationStatus.setText("ALGO");
        while (iter.hasNext()) {
            ArrayList<String> act = iter.next();
            LocalDateTime localDateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                System.out.println(act.get(2));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                localDateTime = LocalDateTime.parse(act.get(2), formatter);
                System.out.println(localDateTime.getDayOfWeek().getValue());
            }


            tvLocationStatus.setText(act.get(3));
            tvLocationDate.setText(act.get(2));
        }
    }

    private void setupPieChart(PieChart pieChart, float[] horasPasadas) {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < horasPasadas.length; i++) {
            entries.add(new PieEntry(horasPasadas[i], diasSemana[i]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Horas pasadas en ubicación cada día");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setCenterText("Horas esta semana");
        pieChart.setEntryLabelTextSize(20);
    }
}
