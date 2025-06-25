package com.example.showmethemoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "money_tracker.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE allusers(email TEXT PRIMARY KEY, password TEXT, name TEXT, gender TEXT, photo TEXT)");


        // Tabel transaksi
        db.execSQL("CREATE TABLE transaksi (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "jenis TEXT, " +               // pemasukan / pengeluaran
                "jumlah INTEGER, " +           // nominal
                "kategori TEXT, " +            // contoh: Gaji, Makanan
                "tanggal TEXT)");              // format: yyyy-MM-dd
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS allusers");
        db.execSQL("DROP TABLE IF EXISTS transaksi");
        onCreate(db);
    }

    // ======================= LOGIN / SIGNUP =======================
    public Boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("allusers", null, values);
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ======================= TRANSAKSI =======================

    // Insert transaksi baru
    public boolean insertTransaksi(String jenis, int jumlah, String kategori, String tanggal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("jenis", jenis);           // "pemasukan" atau "pengeluaran"
        values.put("jumlah", jumlah);
        values.put("kategori", kategori);
        values.put("tanggal", tanggal);       // format: yyyy-MM-dd
        long result = db.insert("transaksi", null, values);
        return result != -1;
    }

    // Total pemasukan/pengeluaran dalam bulan tertentu (yyyy-MM)
    public int getTotalByJenisAndBulan(String jenis, String bulanTahun) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(jumlah) FROM transaksi WHERE jenis = ? AND tanggal LIKE ?",
                new String[]{jenis, bulanTahun + "%"});

        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    // Hitung saldo akhir bulan tertentu
    public int getSaldoAkhir(String bulanTahun) {
        int pemasukan = getTotalByJenisAndBulan("pemasukan", bulanTahun);
        int pengeluaran = getTotalByJenisAndBulan("pengeluaran", bulanTahun);
        return pemasukan - pengeluaran;
    }

    // Mendapatkan semua transaksi pada bulan tertentu
    public Cursor getTransaksiByBulan(String bulanTahun) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transaksi WHERE tanggal LIKE ? ORDER BY tanggal DESC",
                new String[]{bulanTahun + "%"});
    }
}
