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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineChartFragment extends Fragment {

    private LineChart lineChart;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        lineChart = view.findViewById(R.id.lineChart);
        dbHelper = new DatabaseHelper(requireContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String selectedMonth = prefs.getString("selected_month", "2025-07"); // default ke Juli 2025

        List<Entry> pemasukanEntries = new ArrayList<>();
        List<Entry> pengeluaranEntries = new ArrayList<>();

        Cursor cursor = dbHelper.getTransaksiByBulan(selectedMonth);
        Map<Integer, Integer> pemasukanMap = new HashMap<>();
        Map<Integer, Integer> pengeluaranMap = new HashMap<>();

        while (cursor.moveToNext()) {
            String jenis = cursor.getString(cursor.getColumnIndexOrThrow("jenis"));
            int jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah"));
            String tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal")); // format: YYYY-MM-DD

            int day = Integer.parseInt(tanggal.split("-")[2]);

            if (jenis.equalsIgnoreCase("pemasukan")) {
                pemasukanMap.put(day, pemasukanMap.getOrDefault(day, 0) + jumlah);
            } else {
                pengeluaranMap.put(day, pengeluaranMap.getOrDefault(day, 0) + jumlah);
            }
        }
        cursor.close();

        for (int day = 1; day <= 31; day++) {
            if (pemasukanMap.containsKey(day)) {
                pemasukanEntries.add(new Entry(day, pemasukanMap.get(day)));
            }
            if (pengeluaranMap.containsKey(day)) {
                pengeluaranEntries.add(new Entry(day, pengeluaranMap.get(day)));
            }
        }

        LineDataSet set1 = new LineDataSet(pemasukanEntries, "Pemasukan");
        set1.setColor(Color.GREEN);
        set1.setCircleColor(Color.GREEN);

        LineDataSet set2 = new LineDataSet(pengeluaranEntries, "Pengeluaran");
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);

        LineData data = new LineData(set1, set2);
        lineChart.setData(data);
        lineChart.invalidate();

        return view;
    }
}

