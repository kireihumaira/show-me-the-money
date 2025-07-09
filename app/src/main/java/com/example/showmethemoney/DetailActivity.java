package com.example.showmethemoney;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView tvKategoriNama, tvJenis, tvJumlah, tvTanggal, tvCatatan;
    private ImageView iconKategori;
    private Button btnEdit, btnDelete;

    private String kategori, jenis, tanggal, catatan;
    private int jumlah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_transaksi);

        // Inisialisasi view
        tvKategoriNama = findViewById(R.id.tvKategoriNama);
        tvJenis = findViewById(R.id.tvJenis);
        tvJumlah = findViewById(R.id.tvJumlah);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvCatatan = findViewById(R.id.tvCatatan);
        iconKategori = findViewById(R.id.iconKategori);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Ambil data dari intent
        Intent intent = getIntent();
        kategori = intent.getStringExtra("kategori");
        jenis = intent.getStringExtra("jenis");
        jumlah = intent.getIntExtra("jumlah", 0);
        tanggal = intent.getStringExtra("tanggal");
        catatan = intent.getStringExtra("catatan");

        // Format angka dan tanggal
        DecimalFormat formatter = new DecimalFormat("#,###");
        String jumlahFormat = "Rp" + formatter.format(jumlah).replace(",", ".");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id"));
        String tanggalFormat = tanggal;
        try {
            Date date = inputFormat.parse(tanggal);
            tanggalFormat = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set ke UI
        tvKategoriNama.setText(kategori);
        tvJenis.setText(jenis);
        tvJumlah.setText(jumlahFormat);
        tvTanggal.setText(tanggalFormat);
        tvCatatan.setText(catatan);

        // Ganti icon sesuai kategori
        if (kategori.equalsIgnoreCase("Makanan")) {
            iconKategori.setImageResource(R.drawable.ic_makanan);
        } else if (kategori.equalsIgnoreCase("Belanja")) {
            iconKategori.setImageResource(R.drawable.ic_belanja);
        } else if (kategori.equalsIgnoreCase("Telepon")) {
            iconKategori.setImageResource(R.drawable.ic_telepon);
        } else if (kategori.equalsIgnoreCase("Hiburan")) {
            iconKategori.setImageResource(R.drawable.ic_hiburan);
        } else if (kategori.equalsIgnoreCase("Pendidikan")) {
            iconKategori.setImageResource(R.drawable.ic_pendidikan);
        } else if (kategori.equalsIgnoreCase("Kecantikan")) {
            iconKategori.setImageResource(R.drawable.ic_kecantikan);
        } else if (kategori.equalsIgnoreCase("Olahraga")) {
            iconKategori.setImageResource(R.drawable.ic_olahraga);
        } else if (kategori.equalsIgnoreCase("Sosial")) {
            iconKategori.setImageResource(R.drawable.ic_sosial);
        } else if (kategori.equalsIgnoreCase("Transportasi")) {
            iconKategori.setImageResource(R.drawable.ic_transportasi);
        } else if (kategori.equalsIgnoreCase("Pakaian")) {
            iconKategori.setImageResource(R.drawable.ic_pakaian);
        } else if (kategori.equalsIgnoreCase("Mobil")) {
            iconKategori.setImageResource(R.drawable.ic_mobil);
        } else if (kategori.equalsIgnoreCase("Minuman")) {
            iconKategori.setImageResource(R.drawable.ic_minuman);
        } else if (kategori.equalsIgnoreCase("Rokok")) {
            iconKategori.setImageResource(R.drawable.ic_rokok);
        } else if (kategori.equalsIgnoreCase("Elektronik")) {
            iconKategori.setImageResource(R.drawable.ic_elektronik);
        } else if (kategori.equalsIgnoreCase("Bepergian")) {
            iconKategori.setImageResource(R.drawable.ic_bepergian);
        } else if (kategori.equalsIgnoreCase("Kesehatan")) {
            iconKategori.setImageResource(R.drawable.ic_kesehatan);
        } else if (kategori.equalsIgnoreCase("Peliharaan")) {
            iconKategori.setImageResource(R.drawable.ic_peliharaan);
        } else if (kategori.equalsIgnoreCase("Perbaikan")) {
            iconKategori.setImageResource(R.drawable.ic_perbaikan);
        } else if (kategori.equalsIgnoreCase("Perumahan")) {
            iconKategori.setImageResource(R.drawable.ic_perumahan);
        } else if (kategori.equalsIgnoreCase("Rumah")) {
            iconKategori.setImageResource(R.drawable.ic_rumah);
        } else if (kategori.equalsIgnoreCase("Hadiah")) {
            iconKategori.setImageResource(R.drawable.ic_hadiah);
        } else if (kategori.equalsIgnoreCase("Donasi")) {
            iconKategori.setImageResource(R.drawable.ic_donasi);
        } else if (kategori.equalsIgnoreCase("Lotre")) {
            iconKategori.setImageResource(R.drawable.ic_lotre);
        } else if (kategori.equalsIgnoreCase("Anak-Anak")) {
            iconKategori.setImageResource(R.drawable.ic_anak_anak);
        } else if (kategori.equalsIgnoreCase("Investasi")) {
            iconKategori.setImageResource(R.drawable.ic_investasi);
        } else if (kategori.equalsIgnoreCase("Paruh Waktu")) {
            iconKategori.setImageResource(R.drawable.ic_paruh_waktu);
        } else if (kategori.equalsIgnoreCase("Penghargaan")) {
            iconKategori.setImageResource(R.drawable.ic_penghargaan);
        } else if (kategori.equalsIgnoreCase("Lain-Lain")) {
            iconKategori.setImageResource(R.drawable.baseline_add_24);
        } else if (kategori.equalsIgnoreCase("Gaji")) {
            iconKategori.setImageResource(R.drawable.ic_gaji);
        }


        // Tombol back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tombol Edit
        btnEdit.setOnClickListener(v -> {
            BottomSheetInput editSheet = BottomSheetInput.newInstance(jenis, kategori);

            Bundle args = new Bundle();
            args.putString("jenis", jenis);
            args.putString("kategori", kategori);
            args.putString("tanggal", tanggal);
            args.putString("catatan", catatan);
            args.putInt("jumlah", jumlah);
            args.putBoolean("editMode", true); // tambahkan flag edit mode

            editSheet.setArguments(args);

            editSheet.setOnTransaksiSavedListener(() -> finish());

            editSheet.show(getSupportFragmentManager(), "EditTransaksi");
        });

        // Tombol Hapus
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Transaksi")
                    .setMessage("Yakin ingin menghapus transaksi ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        DatabaseHelper db = new DatabaseHelper(this);
                        db.deleteTransaksi(jenis, jumlah, kategori, tanggal, catatan);
                        Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }
}
