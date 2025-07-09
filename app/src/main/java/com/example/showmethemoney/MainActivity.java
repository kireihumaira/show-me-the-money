package com.example.showmethemoney;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    TextView toolbarTitle;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ini penting!
        }


        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            replaceFragment(currentFragment, "Catatan");
        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.catatan) {
                currentFragment = new HomeFragment();
                replaceFragment(currentFragment, "Catatan");
            } else if (itemId == R.id.grafik) {
                currentFragment = new com.example.showmethemoney.fragments.GrafikFragment();
                replaceFragment(currentFragment, "Grafik");
            } else if (itemId == R.id.laporan) {
                currentFragment = new LaporanFragment();
                replaceFragment(currentFragment, "Laporan");
            } else if (itemId == R.id.profile) {
                currentFragment = new ProfileFragment();
                replaceFragment(currentFragment, "Profil");
            }

            return true;
        });

        fab.setOnClickListener(view -> showBottomDialog());
    }

    private void replaceFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();

        toolbarTitle.setText(title);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout layoutPemasukan = dialog.findViewById(R.id.layoutPemasukan);
        LinearLayout layoutPengeluaran = dialog.findViewById(R.id.layoutPengeluaran);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        layoutPemasukan.setOnClickListener(v -> {
            dialog.dismiss();

            BottomSheetPemasukan bottomSheetPemasukan = BottomSheetPemasukan.newInstance("Pemasukan");
            bottomSheetPemasukan.setOnKategoriSelectedListener((jenis, kategori) -> {
                BottomSheetInput input = BottomSheetInput.newInstance(jenis, kategori);
                input.setOnTransaksiSavedListener(() -> {
                    if (currentFragment instanceof HomeFragment) {
                        ((HomeFragment) currentFragment).updateData(); // Update total
                    }
                });
                input.show(getSupportFragmentManager(), "BottomSheetInput");
            });
            bottomSheetPemasukan.show(getSupportFragmentManager(), "BottomSheetPemasukan");
        });

        layoutPengeluaran.setOnClickListener(v -> {
            dialog.dismiss();

            BottomSheetKategori bottomSheetPengeluaran = new BottomSheetKategori("Pengeluaran");
            bottomSheetPengeluaran.setOnKategoriSelectedListener((jenis, kategori) -> {
                BottomSheetInput input = BottomSheetInput.newInstance(jenis, kategori);
                input.setOnTransaksiSavedListener(() -> {
                    if (currentFragment instanceof HomeFragment) {
                        ((HomeFragment) currentFragment).updateData(); // Update total
                    }
                });
                input.show(getSupportFragmentManager(), "BottomSheetInput");
            });
            bottomSheetPengeluaran.show(getSupportFragmentManager(), "BottomSheetKategori");
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
