package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class LaporanFragment extends Fragment {

    private TextView tvTotalPengeluaran, tvTotalPemasukan;
    private TextView tvAnggaran, tvPengeluaran, tvTersisaPersen;
    private CircularProgress progressLingkaran;
    private LinearLayout cardStatistikBulanan;
    private LinearLayout cardAnggaranBulanan;
    private String selectedMonth, selectedYear;

    private final DecimalFormat formatter = new DecimalFormat("#,###");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan, container, false);

        // Statistik bulanan
        tvTotalPengeluaran = view.findViewById(R.id.tvTotalPengeluaran);
        tvTotalPemasukan = view.findViewById(R.id.tvTotalPemasukan);
        cardStatistikBulanan = view.findViewById(R.id.cardStatistikBulanan);

        // Anggaran bulanan
        tvAnggaran = view.findViewById(R.id.tvAnggaran);
        tvPengeluaran = view.findViewById(R.id.tvPengeluaran);
        tvTersisaPersen = view.findViewById(R.id.tvTersisaPersen);
        progressLingkaran = view.findViewById(R.id.progressLingkaran);
        cardAnggaranBulanan = view.findViewById(R.id.cardAnggaranBulanan);

        // Navigasi ke Statistik
        cardStatistikBulanan.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new StatistikBulananFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigasi ke Anggaran Bulanan
        cardAnggaranBulanan.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new FragmentAnggaranBulanan());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Ambil bulan-tahun sekarang
        Calendar calendar = Calendar.getInstance();
        selectedYear = String.valueOf(calendar.get(Calendar.YEAR));
        selectedMonth = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1);

        // Load data
        loadStatistikBulanan();
        loadAnggaranBulanan();

        return view;
    }

    private void loadStatistikBulanan() {
        DatabaseHelper db = new DatabaseHelper(requireContext());

        String bulanTahun = selectedYear + "-" + selectedMonth;
        int totalPengeluaran = db.getTotalByJenisAndBulan("pengeluaran", bulanTahun);
        int totalPemasukan = db.getTotalByJenisAndBulan("pemasukan", bulanTahun);

        tvTotalPengeluaran.setText(formatRupiah(totalPengeluaran));
        tvTotalPemasukan.setText(formatRupiah(totalPemasukan));
    }

    private void loadAnggaranBulanan() {
        DatabaseHelper db = new DatabaseHelper(requireContext());

        String bulanTahun = selectedYear + "-" + selectedMonth;
        int anggaran = db.getAnggaranByBulan(bulanTahun);
        int pengeluaran = db.getTotalByJenisAndBulan("pengeluaran", bulanTahun);
        int tersisa = anggaran - pengeluaran;

        tvTersisaPersen.setText("Tersisa : " + formatRupiah(tersisa));
        tvAnggaran.setText("Anggaran : " + formatRupiah(anggaran));
        tvPengeluaran.setText("Pengeluaran : " + formatRupiah(pengeluaran));

        int persenTersisa = anggaran > 0 ? (int) ((tersisa * 100.0f) / anggaran) : 0;
        if (persenTersisa < 0) persenTersisa = 0;
        progressLingkaran.setProgress(persenTersisa);
    }

    private String formatRupiah(int nominal) {
        return "Rp" + formatter.format(nominal).replace(",", ".");
    }
}
