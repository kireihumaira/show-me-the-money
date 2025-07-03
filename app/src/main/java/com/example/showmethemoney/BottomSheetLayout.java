package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetLayout extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheetlayout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View layoutPengeluaran = view.findViewById(R.id.layoutPengeluaran);
        View layoutPemasukan = view.findViewById(R.id.layoutPemasukan);

        layoutPengeluaran.setOnClickListener(v -> {
            BottomSheetKategori sheet = new BottomSheetKategori("pengeluaran");
            sheet.show(requireActivity().getSupportFragmentManager(), "KategoriSheet");
            dismiss();
        });

        layoutPemasukan.setOnClickListener(v -> {
            BottomSheetKategori sheet = new BottomSheetKategori("pemasukan");
            sheet.show(requireActivity().getSupportFragmentManager(), "KategoriSheet");
            dismiss();
        });
    }
}
