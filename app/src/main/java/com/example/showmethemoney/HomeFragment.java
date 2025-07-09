package com.example.showmethemoney;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvTahun, tvBulan, tvTotalPemasukan, tvTotalPengeluaran;
    private LinearLayout layoutTransaksiPerHari;
    private String selectedYear = "", selectedMonth = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTahun = view.findViewById(R.id.tvTahun);
        tvBulan = view.findViewById(R.id.tvBulan);
        tvTotalPemasukan = view.findViewById(R.id.tvTotalPemasukan);
        tvTotalPengeluaran = view.findViewById(R.id.tvTotalPengeluaran);
        layoutTransaksiPerHari = view.findViewById(R.id.layoutTransaksiPerHari);
        LinearLayout layoutBulan = view.findViewById(R.id.layoutBulan); // bagian klik bulan

        Calendar calendar = Calendar.getInstance();
        selectedYear = String.valueOf(calendar.get(Calendar.YEAR));
        selectedMonth = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1);

        updateHeader();
        updateData();

        layoutBulan.setOnClickListener(v -> showMonthYearPicker());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateHeader() {
        Locale localeID = new Locale("id", "ID");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Integer.parseInt(selectedMonth) - 1);
        tvTahun.setText(selectedYear);
        tvBulan.setText(new SimpleDateFormat("MMMM", localeID).format(cal.getTime()));
    }

    private void showMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pilih Bulan dan Tahun");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);

        NumberPicker npBulan = dialogView.findViewById(R.id.npBulan);
        NumberPicker npTahun = dialogView.findViewById(R.id.npTahun);

        final String[] bulanArray = new String[]{
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };

        npBulan.setMinValue(1);
        npBulan.setMaxValue(12);
        npBulan.setDisplayedValues(bulanArray);
        npBulan.setValue(Integer.parseInt(selectedMonth));

        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        npTahun.setMinValue(2020);
        npTahun.setMaxValue(tahunSekarang + 5);
        npTahun.setValue(Integer.parseInt(selectedYear));

        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedMonth = String.format(Locale.getDefault(), "%02d", npBulan.getValue());
            selectedYear = String.valueOf(npTahun.getValue());
            updateHeader();
            updateData();
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    public void updateData() {
        DatabaseHelper db = new DatabaseHelper(requireContext());
        String bulanTahun = selectedYear + "-" + selectedMonth;

        int pemasukan = db.getTotalByJenisAndBulan("pemasukan", bulanTahun);
        int pengeluaran = db.getTotalByJenisAndBulan("pengeluaran", bulanTahun);

        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalPemasukan.setText("Rp" + formatter.format(pemasukan).replace(",", "."));
        tvTotalPengeluaran.setText("Rp" + formatter.format(pengeluaran).replace(",", "."));

        layoutTransaksiPerHari.removeAllViews();

        Cursor cursor = db.getTransaksiByBulan(bulanTahun);
        LinkedHashMap<String, LinearLayout> groupByTanggal = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> totalPemasukanHarian = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> totalPengeluaranHarian = new LinkedHashMap<>();

        while (cursor.moveToNext()) {
            String tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal"));
            String kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));
            String jenis = cursor.getString(cursor.getColumnIndexOrThrow("jenis"));
            int jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah"));
            String catatan = cursor.getString(cursor.getColumnIndexOrThrow("catatan"));

            if (!groupByTanggal.containsKey(tanggal)) {
                LinearLayout dailyLayout = new LinearLayout(requireContext());
                dailyLayout.setOrientation(LinearLayout.VERTICAL);
                dailyLayout.setPadding(0, 12, 0, 12);

                TextView header = new TextView(requireContext());
                header.setId(View.generateViewId());
                header.setTextColor(0xFF555555);
                header.setTextSize(12);
                header.setTag("header_" + tanggal);
                dailyLayout.addView(header);

                View garis = new View(requireContext());
                garis.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                garis.setBackgroundColor(0xFFA9A9A9);
                dailyLayout.addView(garis);

                groupByTanggal.put(tanggal, dailyLayout);
                layoutTransaksiPerHari.addView(dailyLayout);
            }

            if (jenis.equalsIgnoreCase("pemasukan")) {
                int current = totalPemasukanHarian.getOrDefault(tanggal, 0);
                totalPemasukanHarian.put(tanggal, current + jumlah);
            } else {
                int current = totalPengeluaranHarian.getOrDefault(tanggal, 0);
                totalPengeluaranHarian.put(tanggal, current + jumlah);
            }

            LinearLayout transaksiItem = new LinearLayout(requireContext());
            transaksiItem.setOrientation(LinearLayout.HORIZONTAL);
            transaksiItem.setPadding(0, 40, 0, 24);

            ImageView icon = new ImageView(requireContext());
            icon.setBackgroundResource(R.drawable.bg_kategori_circle);
            icon.setPadding(8, 8, 8, 8);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(80, 80);
            iconParams.setMargins(0, 0, 24, 0);
            icon.setLayoutParams(iconParams);

// Ganti icon sesuai kategori
            if (kategori.equalsIgnoreCase("Makanan")) {
                icon.setImageResource(R.drawable.ic_makanan);
            } else if (kategori.equalsIgnoreCase("Belanja")) {
                icon.setImageResource(R.drawable.ic_belanja);
            } else if (kategori.equalsIgnoreCase("Telepon")) {
                icon.setImageResource(R.drawable.ic_telepon);
            } else if (kategori.equalsIgnoreCase("Hiburan")) {
                icon.setImageResource(R.drawable.ic_hiburan);
            } else if (kategori.equalsIgnoreCase("Pendidikan")) {
                icon.setImageResource(R.drawable.ic_pendidikan);
            } else if (kategori.equalsIgnoreCase("Kecantikan")) {
                icon.setImageResource(R.drawable.ic_kecantikan);
            } else if (kategori.equalsIgnoreCase("Olahraga")) {
                icon.setImageResource(R.drawable.ic_olahraga);
            } else if (kategori.equalsIgnoreCase("Sosial")) {
                icon.setImageResource(R.drawable.ic_sosial);
            } else if (kategori.equalsIgnoreCase("Transportasi")) {
                icon.setImageResource(R.drawable.ic_transportasi);
            } else if (kategori.equalsIgnoreCase("Pakaian")) {
                icon.setImageResource(R.drawable.ic_pakaian);
            } else if (kategori.equalsIgnoreCase("Mobil")) {
                icon.setImageResource(R.drawable.ic_mobil);
            } else if (kategori.equalsIgnoreCase("Minuman")) {
                icon.setImageResource(R.drawable.ic_minuman);
            } else if (kategori.equalsIgnoreCase("Rokok")) {
                icon.setImageResource(R.drawable.ic_rokok);
            } else if (kategori.equalsIgnoreCase("Elektronik")) {
                icon.setImageResource(R.drawable.ic_elektronik);
            } else if (kategori.equalsIgnoreCase("Bepergian")) {
                icon.setImageResource(R.drawable.ic_bepergian);
            } else if (kategori.equalsIgnoreCase("Kesehatan")) {
                icon.setImageResource(R.drawable.ic_kesehatan);
            } else if (kategori.equalsIgnoreCase("Peliharaan")) {
                icon.setImageResource(R.drawable.ic_peliharaan);
            } else if (kategori.equalsIgnoreCase("Perbaikan")) {
                icon.setImageResource(R.drawable.ic_perbaikan);
            } else if (kategori.equalsIgnoreCase("Perumahan")) {
                icon.setImageResource(R.drawable.ic_perumahan);
            } else if (kategori.equalsIgnoreCase("Rumah")) {
                icon.setImageResource(R.drawable.ic_rumah);
            } else if (kategori.equalsIgnoreCase("Hadiah")) {
                icon.setImageResource(R.drawable.ic_hadiah);
            } else if (kategori.equalsIgnoreCase("Donasi")) {
                icon.setImageResource(R.drawable.ic_donasi);
            } else if (kategori.equalsIgnoreCase("Lotre")) {
                icon.setImageResource(R.drawable.ic_lotre);
            } else if (kategori.equalsIgnoreCase("Anak-Anak")) {
                icon.setImageResource(R.drawable.ic_anak_anak);
            } else if (kategori.equalsIgnoreCase("Gaji")) {
                icon.setImageResource(R.drawable.ic_gaji);
            } else if (kategori.equalsIgnoreCase("Investasi")) {
                icon.setImageResource(R.drawable.ic_investasi);
            } else if (kategori.equalsIgnoreCase("Paruh Waktu")) {
                icon.setImageResource(R.drawable.ic_paruh_waktu);
            } else if (kategori.equalsIgnoreCase("Penghargaan")) {
                icon.setImageResource(R.drawable.ic_penghargaan);
            } else if (kategori.equalsIgnoreCase("Lain-Lain")) {
                icon.setImageResource(R.drawable.baseline_add_24);
            } else {
            }

            TextView namaKategori = new TextView(requireContext());
            namaKategori.setText(kategori);
            namaKategori.setTextSize(14);
            namaKategori.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            TextView nominal = new TextView(requireContext());
            nominal.setText((jenis.equalsIgnoreCase("pengeluaran") ? "-" : "") + "Rp" + formatter.format(jumlah).replace(",", "."));
            nominal.setTextSize(14);

            transaksiItem.addView(icon);
            transaksiItem.addView(namaKategori);
            transaksiItem.addView(nominal);

            transaksiItem.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("kategori", kategori);
                intent.putExtra("jenis", jenis);
                intent.putExtra("jumlah", jumlah);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("catatan", catatan);
                startActivity(intent);
            });

            groupByTanggal.get(tanggal).addView(transaksiItem);

            View garisBawah = new View(requireContext());
            LinearLayout.LayoutParams garisParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            garisParams.setMargins(0, 8, 0, 0);
            garisBawah.setLayoutParams(garisParams);
            garisBawah.setBackgroundColor(0xFFD3D3D3);
            groupByTanggal.get(tanggal).addView(garisBawah);
        }

        cursor.close();

        for (String tanggal : groupByTanggal.keySet()) {
            int totalMasuk = totalPemasukanHarian.getOrDefault(tanggal, 0);
            int totalKeluar = totalPengeluaranHarian.getOrDefault(tanggal, 0);

            String formattedTanggal = formatTanggal(tanggal);
            String headerText = formattedTanggal + "     Pengeluaran: Rp" + formatter.format(totalKeluar).replace(",", ".") +
                    "   Pemasukan: Rp" + formatter.format(totalMasuk).replace(",", ".");

            LinearLayout layout = groupByTanggal.get(tanggal);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View view = layout.getChildAt(i);
                if (view instanceof TextView && ("header_" + tanggal).equals(view.getTag())) {
                    ((TextView) view).setText(headerText);
                    break;
                }
            }
        }
    }

    private String formatTanggal(String tanggal) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdfInput.parse(tanggal);
            SimpleDateFormat sdfOutput = new SimpleDateFormat("d MMM", new Locale("id"));
            return sdfOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return tanggal;
        }
    }
}
