package com.example.a1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FragmentMycollect extends Fragment {

    private EditText editText;
    private EditText title;
    private List<TextView> saveTextViews;
    private int currentIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycollect, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button share = view.findViewById(R.id.button2);
        editText = view.findViewById(R.id.editTextText1);
        title = view.findViewById(R.id.editTextText2);

        initTextViewsAndButtons(view);
        loadSavedTexts();

        share.setOnClickListener(v -> {
            saveTextData();
            handleShareClick();
            showSaveConfirmation(view);
            editText.getText().clear();
            title.getText().clear();
            checkAndFixCurrentIndex();
        });

        return view;
    }

    private void handleShareClick() {
        String inputText = editText.getText().toString();
        String titleText = title.getText().toString();
        String text = titleText.isEmpty() ? inputText : titleText + " - \n" + inputText;

        boolean textAdded = false;

        for (TextView textView : saveTextViews) {
            if (textView.getText().toString().isEmpty()) {
                textView.setText(text);
                makeTextViewLinkable(textView);
                textAdded = true;
                break;
            }
        }

        if (!textAdded) {
            showNoMoreSpaceMessage();
        }

        editText.getText().clear();
        title.getText().clear();
    }

// Removed recursive call


    @Override
    public void onResume() {
        super.onResume();
        if (localeChanged()) {
            requireActivity().recreate();
        }
        applyTextSizeFromPreferences();
        updateBackgroundColor();
        loadSavedTexts();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveTextData();
    }

    private void showNoMoreSpaceMessage() {
        Toast.makeText(getActivity(), "No more space to add text!", Toast.LENGTH_SHORT).show();
    }

    private void initTextViewsAndButtons(View view) {
        saveTextViews = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            int textResId = getResources().getIdentifier("save" + i, "id", getContext().getPackageName());
            int buttonResId = getResources().getIdentifier("DeleteButton" + i, "id", getContext().getPackageName());

            TextView tv = view.findViewById(textResId);
            ImageButton btn = view.findViewById(buttonResId);

            if (tv != null && btn != null) {
                saveTextViews.add(tv);
                btn.setOnClickListener(v -> deleteText(saveTextViews.indexOf(tv)));
            } else {
                Log.e("FragmentMycollect", "Component with ID 'save" + i + "' or 'DeleteButton" + i + "' not found.");
            }
        }
    }

    private void deleteText(int index) {
        if (index < saveTextViews.size()) {
            for (int i = index; i < saveTextViews.size() - 1; i++) {
                TextView current = saveTextViews.get(i);
                TextView next = saveTextViews.get(i + 1);
                current.setText(next.getText());
            }
            saveTextViews.get(saveTextViews.size() - 1).setText(""); // Clear the last text view
        }
    }

    private void saveTextData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserTextData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < saveTextViews.size(); i++) {
            TextView tv = saveTextViews.get(i);
            editor.putString("savedText" + i, tv.getText().toString());
        }
        editor.putInt("currentIndex", currentIndex);
        editor.apply();
    }

    private void loadSavedTexts() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserTextData", Context.MODE_PRIVATE);
        for (int i = 0; i < saveTextViews.size(); i++) {
            String text = prefs.getString("savedText" + i, "");
            TextView tv = saveTextViews.get(i);
            if (tv != null) {
                tv.setText(text);
            }
        }
        currentIndex = prefs.getInt("currentIndex", 0);
    }

    private void checkAndFixCurrentIndex() {
        if (currentIndex >= saveTextViews.size()) {
            currentIndex = 0;
        }
    }

    private void showSaveConfirmation(View view) {
        Snackbar.make(view, "Data saved successfully!", Snackbar.LENGTH_SHORT).show();
    }


    private boolean localeChanged() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String currentLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
        String savedLanguage = prefs.getString("user_language", currentLanguage);
        return !currentLanguage.equals(savedLanguage);
    }

    private void applyTextSizeFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(ChangeColorFragment.PREFS_NAME, Context.MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(ChangeColorFragment.TEXT_SIZE_KEY, 18f);  // Default size

        // Ensure textSize does not exceed 18f
        textSize = Math.min(textSize, 18f); // This line restricts the textSize to a maximum of 18f

        // Apply textSize to all text-related views
        Button buttonEnter = requireView().findViewById(R.id.button2);
        EditText editText1 = requireView().findViewById(R.id.editTextText1);
        EditText editText2 = requireView().findViewById(R.id.editTextText2);
        TextView[] textViews = new TextView[]{
                requireView().findViewById(R.id.save1),
                requireView().findViewById(R.id.save2),
                requireView().findViewById(R.id.save3),
                requireView().findViewById(R.id.save4),
                requireView().findViewById(R.id.save5),
                requireView().findViewById(R.id.save6),
                requireView().findViewById(R.id.save7)
        };

        for (TextView textView : textViews) {
            if (textView != null) {
                textView.setTextSize(textSize);
            }
        }
        buttonEnter.setTextSize(textSize);
        editText1.setTextSize(textSize);
        editText2.setTextSize(textSize);
    }



    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        getView().setBackgroundColor(backgroundColor);

        ImageButton imageButton1 = requireView().findViewById(R.id.DeleteButton1);
        imageButton1.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton2 = requireView().findViewById(R.id.DeleteButton2);
        imageButton2.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton3 = requireView().findViewById(R.id.DeleteButton3);
        imageButton3.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton4 = requireView().findViewById(R.id.DeleteButton4);
        imageButton4.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton5 = requireView().findViewById(R.id.DeleteButton5);
        imageButton5.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton6 = requireView().findViewById(R.id.DeleteButton6);
        imageButton6.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton7 = requireView().findViewById(R.id.DeleteButton7);
        imageButton7.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
    }
    private void makeTextViewLinkable(TextView textView) {
        SpannableString spannableString = new SpannableString(textView.getText());
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        // 设置链接颜色（可选）
        textView.setLinkTextColor(Color.BLUE);
    }

}
