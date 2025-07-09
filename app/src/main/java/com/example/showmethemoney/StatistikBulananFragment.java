package com.example.showmethemoney;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class StatistikBulananFragment extends Fragment {

    private TextView tvSaldoTotal, tvPemasukanHeader, tvPengeluaranHeader, tvTahunHeader, tvSemua;
    private LinearLayout layoutDataStatistik;
    private String selectedYear;
    private final String[] bulanList = {
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistik_bulanan, container, false);

        // 🔻 Sembunyikan ActionBar hanya di fragment ini
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        // Inisialisasi view
        tvSaldoTotal = view.findViewById(R.id.tvSaldoTotal);
        tvPengeluaranHeader = view.findViewById(R.id.tvPengeluaranHeader);
        tvPemasukanHeader = view.findViewById(R.id.tvPemasukanHeader);
        tvTahunHeader = view.findViewById(R.id.tvTahunHeader);
        tvSemua = view.findViewById(R.id.tvSemua);
        layoutDataStatistik = view.findViewById(R.id.layoutDataStatistik);
        ImageView btnTahunDropdown = view.findViewById(R.id.btnTahunDropdown);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        // Tahun default sekarang
        selectedYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        tvTahunHeader.setText(selectedYear);
        tvSemua.setText(selectedYear);

        btnTahunDropdown.setOnClickListener(v -> showYearPicker());
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        loadStatistik();

        return view;
    }

    // 🔺 Tampilkan kembali ActionBar saat fragment dihancurkan
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void showYearPicker() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_year_picker);

        NumberPicker yearPicker = dialog.findViewById(R.id.listTahun);
        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);

        yearPicker.setMinValue(tahunSekarang - 10);
        yearPicker.setMaxValue(tahunSekarang);
        yearPicker.setValue(Integer.parseInt(selectedYear));

        yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            selectedYear = String.valueOf(newVal);
        });

        dialog.setOnDismissListener(d -> {
            tvTahunHeader.setText(selectedYear);
            tvSemua.setText(selectedYear);
            loadStatistik();
        });

        dialog.show();
    }

    private void loadStatistik() {
        DatabaseHelper db = new DatabaseHelper(requireContext());

        int totalPemasukan = 0;
        int totalPengeluaran = 0;

        // Bersihkan view sebelumnya (sisakan header 3 pertama)
        layoutDataStatistik.removeViews(3, layoutDataStatistik.getChildCount() - 3);

        for (int i = 1; i <= 12; i++) {
            String bulan = String.format(Locale.getDefault(), "%02d", i);
            String bulanKey = selectedYear + "-" + bulan;

            int pengeluaran = db.getTotalByJenisAndBulan("pengeluaran", bulanKey);
            int pemasukan = db.getTotalByJenisAndBulan("pemasukan", bulanKey);
            int saldo = pemasukan - pengeluaran;

            totalPemasukan += pemasukan;
            totalPengeluaran += pengeluaran;

            addBarisBulan(i - 1, pengeluaran, pemasukan, saldo);
        }

        int totalSaldo = totalPemasukan - totalPengeluaran;

        tvPemasukanHeader.setText("Pemasukkan: Rp " + formatRupiah(totalPemasukan));
        tvPengeluaranHeader.setText("Pengeluaran: Rp " + formatRupiah(totalPengeluaran));
        tvSaldoTotal.setText("Rp " + formatRupiah(totalSaldo));
    }

    private void addBarisBulan(int indexBulan, int pengeluaran, int pemasukan, int saldo) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View row = inflater.inflate(R.layout.item_row_statistik, layoutDataStatistik, false);

        TextView tvBulan = row.findViewById(R.id.tvBulan);
        TextView tvPengeluaran = row.findViewById(R.id.tvPengeluaran);
        TextView tvPemasukan = row.findViewById(R.id.tvPemasukan);
        TextView tvSaldo = row.findViewById(R.id.tvSaldo);

        tvBulan.setText(bulanList[indexBulan]);
        tvPengeluaran.setText("Rp " + formatRupiah(pengeluaran));
        tvPemasukan.setText("Rp " + formatRupiah(pemasukan));
        tvSaldo.setText("Rp " + formatRupiah(saldo));

        layoutDataStatistik.addView(row);
        addDivider();
    }

    private void addDivider() {
        View garis = new View(requireContext());
        garis.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        garis.setBackgroundColor(0xFFD3D3D3);
        layoutDataStatistik.addView(garis);
    }

    private String formatRupiah(int amount) {
        return new DecimalFormat("#,###").format(amount).replace(",", ".");
    }
}
