package com.example.showmethemoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "money_tracker.db";
    private static final int DATABASE_VERSION = 3; // ditingkatkan ke versi 3

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE allusers(" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "name TEXT, " +
                "gender TEXT, " +
                "photo TEXT)");

        db.execSQL("CREATE TABLE transaksi (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "jenis TEXT, " +
                "jumlah INTEGER, " +
                "kategori TEXT, " +
                "tanggal TEXT, " +
                "catatan TEXT)");

        db.execSQL("CREATE TABLE anggaran_bulanan (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bulan TEXT NOT NULL, " +          // format: "2025-07"
                "nominal INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE transaksi ADD COLUMN catatan TEXT");
                Log.d("DB_UPGRADE", "Kolom 'catatan' berhasil ditambahkan.");
            } catch (Exception e) {
                Log.e("DB_UPGRADE", "Gagal menambahkan kolom catatan: " + e.getMessage());
            }
        }

        if (oldVersion < 3) {
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS anggaran_bulanan (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "bulan TEXT NOT NULL, " +
                        "nominal INTEGER NOT NULL)");
                Log.d("DB_UPGRADE", "Tabel 'anggaran_bulanan' berhasil dibuat.");
            } catch (Exception e) {
                Log.e("DB_UPGRADE", "Gagal membuat tabel anggaran: " + e.getMessage());
            }
        }
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

    // ======================= PROFILE =======================
    public boolean updateUser(String email, String name, String gender, String photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("photo", photo);
        int result = db.update("allusers", values, "email = ?", new String[]{email});
        return result > 0;
    }

    // ======================= TRANSAKSI =======================

    public boolean insertTransaksi(String jenis, int jumlah, String kategori, String tanggal, String catatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("jenis", jenis);
        values.put("jumlah", jumlah);
        values.put("kategori", kategori);
        values.put("tanggal", tanggal);
        values.put("catatan", catatan);

        Log.d("INSERT_TRANSAKSI", "jenis=" + jenis + ", jumlah=" + jumlah +
                ", kategori=" + kategori + ", tanggal=" + tanggal + ", catatan=" + catatan);

        long result = db.insert("transaksi", null, values);
        Log.d("INSERT_TRANSAKSI", "Insert result: " + result);
        return result != -1;
    }

    public int getTotalByJenisAndBulan(String jenis, String bulanTahun) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;

        Cursor cursor = db.rawQuery(
                "SELECT SUM(jumlah) FROM transaksi WHERE LOWER(jenis) = ? AND tanggal LIKE ?",
                new String[]{jenis.toLowerCase(), bulanTahun + "%"}
        );

        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    public int getSaldoAkhir(String bulanTahun) {
        int pemasukan = getTotalByJenisAndBulan("pemasukan", bulanTahun);
        int pengeluaran = getTotalByJenisAndBulan("pengeluaran", bulanTahun);
        return pemasukan - pengeluaran;
    }

    public Cursor getTransaksiByBulan(String bulanTahun) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transaksi WHERE tanggal LIKE ? ORDER BY tanggal DESC",
                new String[]{bulanTahun + "%"});
    }

    public boolean deleteTransaksi(String jenis, int jumlah, String kategori, String tanggal, String catatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("transaksi", "jenis=? AND jumlah=? AND kategori=? AND tanggal=? AND catatan=?",
                new String[]{jenis, String.valueOf(jumlah), kategori, tanggal, catatan}) > 0;
    }

    public boolean updateTransaksi(String jenis, int jumlahLama, String kategori, String tanggalLama, String catatanLama,
                                   int jumlahBaru, String tanggalBaru, String catatanBaru) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("jumlah", jumlahBaru);
        values.put("tanggal", tanggalBaru);
        values.put("catatan", catatanBaru);

        int rows = db.update(
                "transaksi",
                values,
                "jenis = ? AND jumlah = ? AND kategori = ? AND tanggal = ? AND catatan = ?",
                new String[]{jenis, String.valueOf(jumlahLama), kategori, tanggalLama, catatanLama}
        );

        Log.d("UPDATE_TRANSAKSI", "Rows updated: " + rows);
        return rows > 0;
    }


    // ======================= ANGGARAN BULANAN =======================

    public int getAnggaranByBulan(String bulanTahun) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nominal FROM anggaran_bulanan WHERE bulan = ?", new String[]{bulanTahun});
        int anggaran = 0;
        if (cursor.moveToFirst()) {
            anggaran = cursor.getInt(0);
        }
        cursor.close();
        return anggaran;
    }

    public void insertOrUpdateAnggaran(String bulanTahun, int nominal) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM anggaran_bulanan WHERE bulan = ?", new String[]{bulanTahun});

        ContentValues values = new ContentValues();
        values.put("bulan", bulanTahun);
        values.put("nominal", nominal);

        if (cursor.moveToFirst()) {
            db.update("anggaran_bulanan", values, "bulan = ?", new String[]{bulanTahun});
        } else {
            db.insert("anggaran_bulanan", null, values);
        }

        cursor.close();
    }
}
