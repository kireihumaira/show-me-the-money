package com.example.showmethemoney;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail;
    private EditText editName, editGender;
    private Button editButton, saveButton, logoutButton;
    private ImageView profileImage;

    private DatabaseHelper dbHelper;
    private String userEmail;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi UI
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        editName = view.findViewById(R.id.edit_name);
        editGender = view.findViewById(R.id.edit_gender);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);
        logoutButton = view.findViewById(R.id.logout_button);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        loadUserData();

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        editButton.setOnClickListener(v -> {
            profileName.setVisibility(View.GONE);
            editName.setVisibility(View.VISIBLE);
            editGender.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        saveButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newGender = editGender.getText().toString().trim();

            // Ambil foto sebelumnya
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT photo FROM allusers WHERE email = ?", new String[]{userEmail});
            String currentPhoto = "";
            if (cursor.moveToFirst()) {
                currentPhoto = cursor.getString(cursor.getColumnIndexOrThrow("photo"));
            }
            cursor.close();

            boolean updated = dbHelper.updateUser(userEmail, newName, newGender, currentPhoto);
            if (updated) {
                Toast.makeText(getContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
                profileName.setText(newName);
                profileName.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);
                editGender.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Hapus session pakai prefs yang sudah ada
                        prefs.edit().remove("email").apply();

                        // Kembali ke LoginActivity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Log.d("PROFILE_FRAGMENT", "onActivityResult called with URI: " + selectedImageUri);

            // Tampilkan gambar
            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(profileImage);

            // Simpan path ke DB
            if (selectedImageUri != null) {
                boolean updated = dbHelper.updateUser(userEmail,
                        editName.getText().toString().trim(),
                        editGender.getText().toString().trim(),
                        selectedImageUri.toString()); // Simpan URI sebagai string

                if (updated) {
                    Toast.makeText(getContext(), "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow("photo"));

            profileName.setText(name != null ? name : "(Tidak ada nama)");
            profileEmail.setText(userEmail);
            editName.setText(name);
            editGender.setText(gender);

            if (photoUrl != null && !photoUrl.isEmpty()) {
                if (photoUrl.startsWith("http") || photoUrl.startsWith("content://")) {
                    Glide.with(requireContext())
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage);
                } else {
                    int resId = getResources().getIdentifier(photoUrl, "drawable", requireContext().getPackageName());
                    if (resId != 0) {
                        Glide.with(requireContext())
                                .load(resId)
                                .circleCrop()
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.ic_profile);
                    }
                }
            }
        }
        cursor.close();
    }
}