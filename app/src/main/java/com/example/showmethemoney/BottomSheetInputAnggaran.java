package com.example.showmethemoney;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetInputAnggaran extends BottomSheetDialogFragment {

    private String kategori;
    private EditText inputNominal;
    private int initialValue = 0; // Tambahan untuk nilai awal

    public BottomSheetInputAnggaran(String kategori) {
        this.kategori = kategori;
    }

    // ✅ Tambahkan method ini untuk set nilai awal
    public void setInitialValue(int value) {
        this.initialValue = value;
    }

    public interface OnAnggaranSetListener {
        void onAnggaranSet(String kategori, String nominal);
    }

    private OnAnggaranSetListener listener;

    public void setOnAnggaranSetListener(OnAnggaranSetListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_input_anggaran, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputNominal = view.findViewById(R.id.inputNominal);
        GridLayout gridKeypad = view.findViewById(R.id.gridKeypad);
        ImageView btnSelesai = view.findViewById(R.id.btnSelesai);

        // ✅ Set nilai awal jika ada
        if (initialValue > 0) {
            inputNominal.setText(String.valueOf(initialValue));
            inputNominal.setSelection(inputNominal.getText().length()); // Pindah cursor ke akhir
        }

        setupKeypad(gridKeypad);

        btnSelesai.setOnClickListener(v -> {
            String nominal = inputNominal.getText().toString().trim();
            if (TextUtils.isEmpty(nominal)) {
                Toast.makeText(getContext(), "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onAnggaranSet(kategori, nominal);
            }

            dismiss();
        });
    }

    private void setupKeypad(GridLayout gridKeypad) {
        int childCount = gridKeypad.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = gridKeypad.getChildAt(i);

            if (child instanceof ImageView || child instanceof android.widget.Button) {
                child.setOnClickListener(v -> {
                    String text = ((android.widget.Button) v).getText().toString();

                    switch (text) {
                        case "C":
                            inputNominal.setText("");
                            break;
                        case "←":
                            String current = inputNominal.getText().toString();
                            if (!current.isEmpty()) {
                                inputNominal.setText(current.substring(0, current.length() - 1));
                                inputNominal.setSelection(inputNominal.getText().length());
                            }
                            break;
                        default:
                            inputNominal.append(text);
                            break;
                    }
                });
            }
        }
    }
}
