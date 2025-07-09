package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PengaturanAnggaranFragment extends Fragment {

    private LinearLayout listAnggaran;

    // Data kategori dan icon
    String[] kategoriList = {"Makanan", "Belanja", "Telepon", "Hiburan", "Pendidikan", "Kecantikan", "Olahraga", "Sosial"};
    int[] iconList = {
            R.drawable.ic_makanan,
            R.drawable.ic_belanja,
            R.drawable.ic_telepon,
            R.drawable.ic_hiburan,
            R.drawable.ic_pendidikan,
            R.drawable.ic_kecantikan,
            R.drawable.ic_olahraga,
            R.drawable.ic_sosial
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pengaturan_anggaran, container, false);

        listAnggaran = view.findViewById(R.id.listAnggaran);
        populateAnggaranList(inflater);

        return view;
    }

    private void populateAnggaranList(LayoutInflater inflater) {
        for (int i = 0; i < kategoriList.length; i++) {
            View itemView = inflater.inflate(R.layout.item_kategori_anggaran, listAnggaran, false);

            TextView tvKategori = itemView.findViewById(R.id.tvKategori);
            TextView tvAnggaranValue = itemView.findViewById(R.id.tvAnggaranValue);
            ImageView ivIcon = itemView.findViewById(R.id.ivIconKategori);
            ImageView ivDelete = itemView.findViewById(R.id.ivDelete);

            // Set nama dan icon kategori
            tvKategori.setText(kategoriList[i]);
            ivIcon.setImageResource(iconList[i]);

            // Simulasikan apakah data sudah diisi
            boolean sudahDiisi = false; // nanti ubah sesuai kondisi

            if (sudahDiisi) {
                tvAnggaranValue.setText("Rp 100.000");
                ivDelete.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvAnggaranValue.setText("Edit");
                ivDelete.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            }

            listAnggaran.addView(itemView);
        }
    }
}
