package com.example.showmethemoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.animation.Easing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PieChartFragment extends Fragment {

    private PieChart pieChart;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        dbHelper = new DatabaseHelper(requireContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String selectedMonth = prefs.getString("selected_month", "2025-07");

        Cursor cursor = dbHelper.getTransaksiByBulan(selectedMonth);
        Map<String, Integer> kategoriMap = new HashMap<>();

        while (cursor.moveToNext()) {
            String jenis = cursor.getString(cursor.getColumnIndexOrThrow("jenis"));
            if (!jenis.equalsIgnoreCase("pengeluaran")) continue;

            int jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah"));
            String kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));
            kategoriMap.put(kategori, kategoriMap.getOrDefault(kategori, 0) + jumlah);
        }
        cursor.close();

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : kategoriMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Pengeluaran");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate();

        return view;
    }
}
