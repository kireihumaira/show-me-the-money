package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    TextView textPemasukan, textPengeluaran, textSaldo, textMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textPemasukan = view.findViewById(R.id.textPemasukan);
        textPengeluaran = view.findViewById(R.id.textPengeluaran);
        textSaldo = view.findViewById(R.id.textSaldo);
        textMonth = view.findViewById(R.id.textMonth);

        // Tanggal sekarang dalam format yyyy-MM
        String bulanIni = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        String bulanUntukDisplay = new SimpleDateFormat("MMMM yyyy", new Locale("id")).format(new Date());
        textMonth.setText(bulanUntukDisplay);

        // Ambil data dari database
        DatabaseHelper db = new DatabaseHelper(requireContext());
        int pemasukan = db.getTotalByJenisAndBulan("pemasukan", bulanIni);
        int pengeluaran = db.getTotalByJenisAndBulan("pengeluaran", bulanIni);
        int saldo = pemasukan - pengeluaran;

        // Tampilkan
        textPemasukan.setText("Pemasukan: Rp " + pemasukan);
        textPengeluaran.setText("Pengeluaran: Rp " + pengeluaran);
        textSaldo.setText("Saldo: Rp " + saldo);

        return view;
    }
}
