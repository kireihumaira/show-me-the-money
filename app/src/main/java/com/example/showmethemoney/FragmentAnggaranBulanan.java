package com.example.showmethemoney;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentAnggaranBulanan extends Fragment {

    private TextView tvSelectedMonthYear;
    private LinearLayout layoutListKategori;
    private DatabaseHelper dbHelper;
    private String bulan;

    // Tambahan
    private View headerLayout;
    private View toolbar;
    private TextView toolbarTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anggaran_bulanan, container, false);

        // Inisialisasi database helper
        dbHelper = new DatabaseHelper(requireContext());

        // Inisialisasi komponen dari fragment
        tvSelectedMonthYear = view.findViewById(R.id.tvSelectedMonthYear);
        layoutListKategori = view.findViewById(R.id.layoutListKategori);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        View layoutMonthPicker = view.findViewById(R.id.layoutMonthPicker);
        View btnPengaturanAnggaran = view.findViewById(R.id.btnPengaturanAnggaran);

        // Tampilkan header pink (dari XML) dan sembunyikan toolbar utama
        headerLayout = view.findViewById(R.id.headerLayout);
        toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title);

        if (headerLayout != null) {
            headerLayout.setVisibility(View.VISIBLE);
        }
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
        if (toolbarTitle != null) {
            toolbarTitle.setVisibility(View.GONE);
        }

        // Set bulan sekarang
        bulan = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String displayBulan = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        tvSelectedMonthYear.setText(displayBulan);

        // Isi daftar kategori anggaran
        updateListKategori();

        // Tampilkan DatePicker saat diklik
        layoutMonthPicker.setOnClickListener(v -> showMonthYearPicker());

        // Navigasi ke pengaturan anggaran
        btnPengaturanAnggaran.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new PengaturanAnggaranFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // Tombol kembali
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void showMonthYearPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            bulan = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.getTime());
            String displayBulan = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.getTime());
            tvSelectedMonthYear.setText(displayBulan);
            updateListKategori();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Sembunyikan pemilihan hari
        try {
            int dayField = getResources().getIdentifier("android:id/day", null, null);
            if (dayField != 0) {
                View dayView = dialog.getDatePicker().findViewById(dayField);
                if (dayView != null) dayView.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}

        dialog.show();
    }

    private void updateListKategori() {
        layoutListKategori.removeAllViews();
        List<String> daftarKategori = dbHelper.getAllKategoriFromAnggaran(bulan);

        for (String kategori : daftarKategori) {
            int anggaran = dbHelper.getAnggaranByKategori(bulan, kategori);
            int pengeluaran = dbHelper.getTotalPengeluaranByKategori(bulan, kategori);
            int sisa = anggaran - pengeluaran;

            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_card_anggaran_kategori, layoutListKategori, false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);

            ((TextView) card.findViewById(R.id.tvKategori)).setText(kategori);
            ((TextView) card.findViewById(R.id.tvAnggaran)).setText("Rp " + anggaran);
            ((TextView) card.findViewById(R.id.tvPengeluaran)).setText("Rp " + pengeluaran);
            ((TextView) card.findViewById(R.id.tvSisa)).setText("Rp " + sisa);

            CircularProgress progressView = card.findViewById(R.id.progressLingkaran);
            TextView tvProgressLabel = card.findViewById(R.id.tvProgressLabel);

            if (anggaran > 0) {
                if (pengeluaran <= anggaran) {
                    float persentase = ((float) sisa / anggaran) * 100f;
                    progressView.setProgress(persentase);
                    tvProgressLabel.setText("Tersisa\n" + Math.round(persentase) + "%");
                } else {
                    progressView.setProgress(0f);
                    tvProgressLabel.setText("Melebihi");
                }
            } else {
                progressView.setProgress(0f);
                tvProgressLabel.setText("-");
            }

            layoutListKategori.addView(card);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Tampilkan kembali toolbar dan title saat fragment ditutup
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }
        if (toolbarTitle != null) {
            toolbarTitle.setVisibility(View.VISIBLE);
        }
    }
}
