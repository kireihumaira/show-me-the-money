package com.example.showmethemoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;


public class FragmentAnggaranBulanan extends Fragment {

    private TextView tvSelectedMonthYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anggaran_bulanan, container, false);

        // Hide ActionBar dari MainActivity
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        ImageView btnBack = view.findViewById(R.id.btnBack);
        tvSelectedMonthYear = view.findViewById(R.id.tvSelectedMonthYear);
        View layoutMonthPicker = view.findViewById(R.id.layoutMonthPicker);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        layoutMonthPicker.setOnClickListener(v -> showMonthYearPicker());

        return view;
    }

    private void showMonthYearPicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);

                    SimpleDateFormat format = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                    tvSelectedMonthYear.setText(format.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Hide day selector
        try {
            int dayField = getResources().getIdentifier("android:id/day", null, null);
            if (dayField != 0) {
                View dayView = dialog.getDatePicker().findViewById(dayField);
                if (dayView != null) dayView.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show ActionBar again
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }
}
