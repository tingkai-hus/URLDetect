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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.Locale;

public class ChangeLanguageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changelanguage, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonEnglish = view.findViewById(R.id.english);
        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocale("en", "US");
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonChinese = view.findViewById(R.id.chinese);
        buttonChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocale("zh", "TW");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBackgroundColor();
        applyTextSizeFromPreferences();
    }

    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        requireView().findViewById(R.id.changelanguagebackground).setBackgroundColor(backgroundColor);
    }

    private void saveLocale(String languageCode, String countryCode) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_language", languageCode);
        editor.putString("user_country", countryCode);
        editor.apply();
        requireActivity().recreate();
    }

    private void applyTextSizeFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(ChangeColorFragment.PREFS_NAME, Context.MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(ChangeColorFragment.TEXT_SIZE_KEY, 18f);

        Button buttonEnglish = requireView().findViewById(R.id.english);
        Button buttonChinese = requireView().findViewById(R.id.chinese);
        buttonEnglish.setTextSize(textSize);
        buttonChinese.setTextSize(textSize);
    }

    @Override
    public void onAttach(@NonNull Context context) {
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

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
