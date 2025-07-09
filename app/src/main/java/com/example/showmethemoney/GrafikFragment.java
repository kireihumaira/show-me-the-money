package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GrafikFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Spinner monthSpinner, yearSpinner;
    private GrafikPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grafik, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        GrafikPagerAdapter adapter = new GrafikPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Grafik Keuangan");
            else if (position == 1) tab.setText("Daftar Pengeluaran");
        }).attach();
        monthSpinner = view.findViewById(R.id.month_spinner);
        yearSpinner = view.findViewById(R.id.year_spinner);
        viewPager = view.findViewById(R.id.viewPager);

        pagerAdapter = new GrafikPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i)); // hasil: "01", "02", ..., "12"
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Set tahun sekarang + mundur 5 tahun
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        AdapterView.OnItemSelectedListener onDateChange = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bulan = monthSpinner.getSelectedItem().toString();
                String tahun = yearSpinner.getSelectedItem().toString();
                String bulanTahun = tahun + "-" + bulan;

                // Simpan ke SharedPreferences (biar bisa diakses LineChartFragment & PieChartFragment)
                SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                prefs.edit().putString("selected_month", bulanTahun).apply();

                // Refresh fragment
                Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
                if (currentFragment instanceof LineChartFragment) {
                    ((LineChartFragment) currentFragment).loadChart();
                } else if (currentFragment instanceof PieChartFragment) {
                    ((PieChartFragment) currentFragment).loadChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };

        monthSpinner.setOnItemSelectedListener(onDateChange);
        yearSpinner.setOnItemSelectedListener(onDateChange);

        return view;
    }
}
