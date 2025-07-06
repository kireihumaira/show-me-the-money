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

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail;
    private EditText editName, editGender;
    private Button editButton, saveButton, logoutButton;
    private ImageView profileImage;

    private DatabaseHelper dbHelper;
    private String userEmail;

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
            // Hapus session
            prefs.edit().remove("email").apply();

            // Kembali ke LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
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
                if (photoUrl.startsWith("http")) {
                    // Jika photo berupa URL
                    Glide.with(requireContext())
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage);
                } else {
                    // Coba load dari drawable (contoh: "ic_profile")
                    int resId = getResources().getIdentifier(photoUrl, "drawable", requireContext().getPackageName());
                    if (resId != 0) {
                        Glide.with(requireContext())
                                .load(resId)
                                .circleCrop()
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.ic_profile); // fallback
                    }
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_profile); // fallback default
            }
            cursor.close();
        }
    }
}
