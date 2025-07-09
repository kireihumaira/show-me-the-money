package com.example.showmethemoney;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BottomSheetInput extends BottomSheetDialogFragment {

    private TextView tvNominal;
    private StringBuilder nominalBuilder = new StringBuilder();
    private EditText etCatatan;
    private ImageView btnUpload, btnTanggal, btnSelesai;
    private GridLayout gridKeypad;

    private String jenis = "pemasukan";
    private String kategori = "Lainnya";
    private String selectedDate = "";

    private boolean isEditMode = false;
    private int jumlahLama = 0;
    private String catatanLama = "";
    private String tanggalLama = "";

    private DatabaseHelper dbHelper;

    public interface OnTransaksiSavedListener {
        void onTransaksiSaved();
    }

    private OnTransaksiSavedListener listener;

    public void setOnTransaksiSavedListener(OnTransaksiSavedListener listener) {
        this.listener = listener;
    }

    public static BottomSheetInput newInstance(String jenis, String kategori) {
        BottomSheetInput fragment = new BottomSheetInput();
        Bundle args = new Bundle();
        args.putString("jenis", jenis);
        args.putString("kategori", kategori);
        fragment.setArguments(args);
        return fragment;
    }

    public void setEditData(int jumlah, String tanggal, String catatan) {
        this.jumlahLama = jumlah;
        this.tanggalLama = tanggal;
        this.catatanLama = catatan;
        this.isEditMode = true;
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                        Toast.makeText(getContext(), "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvNominal = view.findViewById(R.id.tvNominal);
        etCatatan = view.findViewById(R.id.etCatatan);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnTanggal = view.findViewById(R.id.btnTanggal);
        btnSelesai = view.findViewById(R.id.btnSelesai);
        gridKeypad = view.findViewById(R.id.gridKeypad);

        dbHelper = new DatabaseHelper(requireContext());

        if (getArguments() != null) {
            kategori = getArguments().getString("kategori", "Lainnya");
            jenis = getArguments().getString("jenis", "pemasukan");
            isEditMode = getArguments().getBoolean("editMode", false);

            if (isEditMode) {
                jumlahLama = getArguments().getInt("jumlah");
                catatanLama = getArguments().getString("catatan", "");
                tanggalLama = getArguments().getString("tanggal", "");

                nominalBuilder = new StringBuilder(String.valueOf(jumlahLama));
                DecimalFormat formatter = new DecimalFormat("#,###");
                String display = "Rp" + formatter.format(jumlahLama).replace(",", ".");
                tvNominal.setText(display);
                etCatatan.setText(catatanLama);
                selectedDate = tanggalLama;

                Toast.makeText(getContext(), "Edit data transaksi", Toast.LENGTH_SHORT).show();
            }
        }

        btnUpload.setOnClickListener(v -> showImagePicker());
        btnTanggal.setOnClickListener(v -> showDatePicker());
        btnSelesai.setOnClickListener(v -> saveToDatabase());

        int childCount = gridKeypad.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = gridKeypad.getChildAt(i);
            if (child instanceof Button) {
                String input = ((Button) child).getText().toString();
                child.setOnClickListener(v -> handleKeypadInput(input));
            }
        }
    }


    private void showImagePicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(selected.getTime());
                    Toast.makeText(getContext(), "Tanggal dipilih: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void handleKeypadInput(String input) {
        switch (input) {
            case "C":
                nominalBuilder.setLength(0);
                break;
            case "←":
                if (nominalBuilder.length() > 0)
                    nominalBuilder.deleteCharAt(nominalBuilder.length() - 1);
                break;
            default:
                nominalBuilder.append(input);
                break;
        }

        String display;
        if (nominalBuilder.length() > 0) {
            try {
                long angka = Long.parseLong(nominalBuilder.toString());
                DecimalFormat formatter = new DecimalFormat("#,###");
                display = "Rp" + formatter.format(angka).replace(",", ".");
            } catch (NumberFormatException e) {
                display = "Rp0";
            }
        } else {
            display = "Rp0";
        }

        tvNominal.setText(display);
    }

    private void saveToDatabase() {
        if (nominalBuilder.length() == 0) {
            Toast.makeText(getContext(), "Masukkan nominal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(getContext(), "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlahBaru = Integer.parseInt(nominalBuilder.toString());
        String catatanBaru = etCatatan.getText().toString().trim();
        boolean sukses;

        if (isEditMode) {
            sukses = dbHelper.updateTransaksi(
                    jenis, jumlahLama, kategori, tanggalLama, catatanLama,
                    jumlahBaru, selectedDate, catatanBaru
            );
        } else {
            sukses = dbHelper.insertTransaksi(jenis, jumlahBaru, kategori, selectedDate, catatanBaru);
        }

        if (sukses) {
            Toast.makeText(getContext(), isEditMode ? "Transaksi berhasil diperbarui" : "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onTransaksiSaved();
            dismiss();
        } else {
            Toast.makeText(getContext(), "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showImagePicker();
        }
    }
}
