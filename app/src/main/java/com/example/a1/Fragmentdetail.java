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
import androidx.navigation.Navigation;

import java.util.Locale;

public class Fragmentdetail extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String mParam1;

    public Fragmentdetail() {
        // Required empty public constructor
    }

    public static Fragmentdetail newInstance(String param1, String param2) {
        Fragmentdetail fragment = new Fragmentdetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentdetail, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        view.findViewById(R.id.changecolor).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentdetail_to_changeColorFragment));
        view.findViewById(R.id.changelanuage).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentdetail_to_changeLanguageFragment));
        view.findViewById(R.id.changesize).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentdetail_to_changeSizeFragment));
/*
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button button1 = view.findViewById(R.id.changecolor);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理導航至 ChangeColorFragment 的程式碼
                navigateToFragment(new ChangeColorFragment());
            }
        });

        Button button2 = view.findViewById(R.id.changelanuage);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理導航至 ChangeLanguageFragment 的程式碼
                navigateToFragment(new ChangeLanguageFragment());
            }
        });

        Button button3 = view.findViewById(R.id.changesize);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理導航至 ChangeFontSizeFragment 的程式碼
                navigateToFragment(new ChangeSizeFragment());
            }
        });

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        if (fragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settingbackground, fragment)
                    .addToBackStack(null)
                    .commit();
        }*/
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateBackgroundColor();
        if (localeChanged()) {
            requireActivity().recreate();
        }
        applyTextSizeFromPreferences();
    }

    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        requireView().findViewById(R.id.settingbackground).setBackgroundColor(backgroundColor);
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(ChangeColorFragment.PREFS_NAME, Context.MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(ChangeColorFragment.TEXT_SIZE_KEY, 18f);

        Button buttonColor = requireView().findViewById(R.id.changecolor);
        Button buttonLanguage = requireView().findViewById(R.id.changelanuage);
        Button buttonSize = requireView().findViewById(R.id.changesize);
        buttonColor.setTextSize(textSize);
        buttonLanguage.setTextSize(textSize);
        buttonSize.setTextSize(textSize);
    }
}
