package com.example.showmethemoney;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetPemasukan extends BottomSheetDialogFragment {

    private String jenis;

    public interface OnKategoriSelectedListener {
        void onKategoriSelected(String jenis, String kategori);
    }

    private OnKategoriSelectedListener listener;

    public void setOnKategoriSelectedListener(OnKategoriSelectedListener listener) {
        this.listener = listener;
    }

    public static BottomSheetPemasukan newInstance(String jenis) {
        BottomSheetPemasukan fragment = new BottomSheetPemasukan();
        Bundle args = new Bundle();
        args.putString("jenis", jenis);
        fragment.setArguments(args);
        return fragment;
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
        jenis = getArguments() != null ? getArguments().getString("jenis") : "pemasukan";

        GridLayout gridLayout = view.findViewById(R.id.gridKategori);

        String[] kategoriList = {"Gaji", "Investasi", "Paruh Waktu", "Penghargaan", "Lain-Lain"};
        int[] iconList = {
                R.drawable.ic_gaji,
                R.drawable.ic_investasi,
                R.drawable.ic_paruh_waktu,
                R.drawable.ic_penghargaan,
                R.drawable.baseline_add_24
        };

        for (int i = 0; i < kategoriList.length; i++) {
            String kategori = kategoriList[i];
            int iconRes = iconList[i];

            // Parent Layout tiap item
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);
            itemLayout.setPadding(8, 8, 8, 8);

            // Set layout width 0 + columnSpec 1f (biar 4 item rata)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            itemLayout.setLayoutParams(params);

            // Icon gambar
            ImageView icon = new ImageView(getContext());
            icon.setImageResource(iconRes);
            icon.setBackgroundResource(R.drawable.bg_kategori_circle);
            icon.setPadding(32, 32, 32, 32);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(150, 150);
            iconParams.bottomMargin = 8;
            icon.setLayoutParams(iconParams);

            // Teks nama kategori
            TextView text = new TextView(getContext());
            text.setText(kategori);
            text.setTextColor(getResources().getColor(android.R.color.black));
            text.setGravity(Gravity.CENTER);
            text.setTextSize(14);

            itemLayout.addView(icon);
            itemLayout.addView(text);

            itemLayout.setOnClickListener(v -> {
                dismiss();
                if (listener != null) {
                    listener.onKategoriSelected(jenis, kategori);
                }
            });

            gridLayout.addView(itemLayout);
        }
    }
}
