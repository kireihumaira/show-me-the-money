package com.example.showmethemoney;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PengaturanAnggaranFragment extends Fragment {

    private LinearLayout listAnggaran;
    private DatabaseHelper dbHelper;
    private final HashMap<String, View> kategoriItemViews = new HashMap<>();

    // View global untuk mengontrol header dan toolbar
    private View headerLayout;
    private TextView toolbarTitle;

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

        // Sembunyikan action bar agar tidak dobel header
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        // Sembunyikan header merah dan toolbar title jika ada
        headerLayout = requireActivity().findViewById(R.id.headerLayout);
        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title);

        if (headerLayout != null) headerLayout.setVisibility(View.GONE);
        if (toolbarTitle != null) toolbarTitle.setVisibility(View.GONE);

        // Tombol back
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        listAnggaran = view.findViewById(R.id.listAnggaran);
        dbHelper = new DatabaseHelper(requireContext());

        populateAnggaranList(inflater);

        return view;
    }

    private void populateAnggaranList(LayoutInflater inflater) {
        listAnggaran.removeAllViews();
        String bulan = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        for (int i = 0; i < kategoriList.length; i++) {
            String kategori = kategoriList[i];
            int iconRes = iconList[i];

            View itemView = inflater.inflate(R.layout.item_kategori_anggaran, listAnggaran, false);

            TextView tvKategori = itemView.findViewById(R.id.tvKategori);
            TextView tvAnggaranValue = itemView.findViewById(R.id.tvAnggaranValue);
            ImageView ivIcon = itemView.findViewById(R.id.ivIconKategori);
            ImageView ivDelete = itemView.findViewById(R.id.ivDelete);
            FrameLayout iconBackground = (FrameLayout) ivIcon.getParent();

            tvKategori.setText(kategori);
            ivIcon.setImageResource(iconRes);

            int nominal = dbHelper.getAnggaranByKategori(bulan, kategori);
            if (nominal > 0) {
                tvAnggaranValue.setText("Rp " + formatNominal(nominal));
                ivDelete.setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
                iconBackground.setBackgroundResource(R.drawable.bg_kategori_circle_pink);
                ivIcon.setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_IN);
            } else {
                tvAnggaranValue.setText("Edit");
                ivDelete.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
                iconBackground.setBackgroundResource(R.drawable.bg_kategori_circle);
                ivIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            }

            itemView.setOnClickListener(v -> {
                showInputAnggaranDialog(kategori, tvAnggaranValue, ivDelete, ivIcon, iconBackground);
            });

            ivDelete.setOnClickListener(v -> {
                dbHelper.deleteAnggaran(bulan, kategori);
                tvAnggaranValue.setText("Edit");
                ivDelete.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
                iconBackground.setBackgroundResource(R.drawable.bg_kategori_circle);
                ivIcon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            });

            kategoriItemViews.put(kategori, itemView);
            listAnggaran.addView(itemView);
        }
    }

    private void showInputAnggaranDialog(String kategori, TextView tvAnggaranValue, ImageView ivDelete, ImageView ivIcon, FrameLayout iconBackground) {
        BottomSheetInputAnggaran bottomSheet = new BottomSheetInputAnggaran(kategori);
        bottomSheet.setOnAnggaranSetListener((kategoriName, nominalStr) -> {
            if (nominalStr == null || nominalStr.trim().isEmpty()) {
                Toast.makeText(getContext(), "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            int nominal;
            try {
                nominal = Integer.parseInt(nominalStr.trim());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Nominal harus berupa angka", Toast.LENGTH_SHORT).show();
                return;
            }

            String bulan = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
            dbHelper.insertOrUpdateAnggaran(bulan, kategoriName, nominal);

            // Update tampilan
            tvAnggaranValue.setText("Rp " + formatNominal(nominal));
            ivDelete.setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
            iconBackground.setBackgroundResource(R.drawable.bg_kategori_circle_pink);
            ivIcon.setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_IN);
        });

        bottomSheet.show(requireActivity().getSupportFragmentManager(), "BottomSheetInputAnggaran");
    }

    private String formatNominal(int nominal) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(nominal).replace(',', '.');
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Tampilkan kembali action bar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        // Tampilkan kembali header merah dan toolbar title
        if (headerLayout != null) headerLayout.setVisibility(View.VISIBLE);
        if (toolbarTitle != null) toolbarTitle.setVisibility(View.VISIBLE);
    }
}
