package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetKategori extends BottomSheetDialogFragment {

    private final String jenis;

    public BottomSheetKategori(String jenis) {
        this.jenis = jenis;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_kategori, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridLayout gridLayout = view.findViewById(R.id.gridKategori);

        String[] kategoriList = {
                "Makanan", "Transport", "Belanja", "Kesehatan", "Pendidikan", "Hiburan"
        };

        for (String kategori : kategoriList) {
            TextView item = new TextView(getContext());
            item.setText(kategori);
            item.setPadding(24, 24, 24, 24);
            item.setBackgroundResource(R.drawable.bg_kategori_circle); // bikin drawable ini
            item.setTextColor(getResources().getColor(android.R.color.black));
            item.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            item.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Kategori: " + kategori + " (" + jenis + ")", Toast.LENGTH_SHORT).show();
                dismiss();
            });

            gridLayout.addView(item);
        }
    }
}
