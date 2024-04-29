package com.example.a1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ChangeSizeFragment extends Fragment {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String TEXT_SIZE_KEY = "textSize";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_size, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(ChangeColorFragment.PREFS_NAME, Context.MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(ChangeColorFragment.TEXT_SIZE_KEY, 18f);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonLarge = view.findViewById(R.id.large);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonMedium = view.findViewById(R.id.medium);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonSmall = view.findViewById(R.id.small);

        buttonLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextSize(24f);
            }
        });

        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextSize(18f);
            }
        });

        buttonSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextSize(12f);
            }
        });

        buttonLarge.setTextSize(textSize);
        buttonMedium.setTextSize(textSize);
        buttonSmall.setTextSize(textSize);

        return view;
    }

    private void updateTextSize(float size) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(TEXT_SIZE_KEY, size).apply();
        requireActivity().recreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);

        requireView().findViewById(R.id.changesizebackground).setBackgroundColor(backgroundColor);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateLocale(context);
    }

    private void updateLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String language = prefs.getString("user_language", "en");
        String country = prefs.getString("user_country", "US");
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
    }
}
