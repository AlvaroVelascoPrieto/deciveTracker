package com.example.entrega.view;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entrega.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class LocationStats extends AppCompatActivity {

    // Variables de ejemplo (reemplaza con tus propios datos)
    private float[] horasPasadas = {7.5f, 8.0f, 7.8f, 8.2f, 7.7f}; // Horas pasadas en la ubicación cada día
    private String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_stats);

        // Configurar el gráfico de pastel
        PieChart pieChart = findViewById(R.id.pieChart);
        setupPieChart(pieChart, horasPasadas);
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
    }
}
