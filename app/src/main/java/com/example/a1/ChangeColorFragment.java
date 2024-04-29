package com.example.a1;

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

public class ChangeColorFragment extends Fragment {

    public static final String PREFS_NAME = "AppPreferences";
    public static final String TEXT_SIZE_KEY = "textSize";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changecolor, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // 更新背景颜色
        updateBackgroundColor(view);
        Button blackButton = view.findViewById(R.id.black);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int blackColor = ContextCompat.getColor(requireContext(), R.color.黑);
                saveBackgroundColorPreference(blackColor);
                updateBackgroundColor(view);
            }
        });

        Button whiteButton = view.findViewById(R.id.white);
        whiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whiteColor = ContextCompat.getColor(requireContext(), R.color.灰);
                saveBackgroundColorPreference(whiteColor);
                updateBackgroundColor(view);
            }
        });

        Button colorfulButton = view.findViewById(R.id.colorful);
        colorfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whiteColor = ContextCompat.getColor(requireContext(), R.color.背景);
                saveBackgroundColorPreference(whiteColor);
                updateBackgroundColor(view);
            }
        });

        return view;
    }

    private void updateBackgroundColor(View view) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        view.findViewById(R.id.changecolorbackground).setBackgroundColor(backgroundColor);
    }

    private void saveBackgroundColorPreference(int color) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        prefs.edit().putInt("background_color", color).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (localeChanged()) {
            requireActivity().recreate();
        }
        applyTextSizeFromPreferences();
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
        context = context.createConfigurationContext(config);
    }

    private boolean localeChanged() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String currentLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
        String savedLanguage = prefs.getString("user_language", currentLanguage);
        return !currentLanguage.equals(savedLanguage);
    }

    private void applyTextSizeFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(TEXT_SIZE_KEY, 18f);

        Button blackButton = requireView().findViewById(R.id.black);
        Button whiteButton = requireView().findViewById(R.id.white);
        Button colorfulButton = requireView().findViewById(R.id.colorful);
        blackButton.setTextSize(textSize);
        whiteButton.setTextSize(textSize);
        colorfulButton.setTextSize(textSize);
    }
}
