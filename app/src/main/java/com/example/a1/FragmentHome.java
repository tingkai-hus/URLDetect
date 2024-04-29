package com.example.a1;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;


public class FragmentHome extends Fragment {

    private Button button;
    private EditText urlEditText;
    private ImageButton imageButton2;
    private ScrollView scrollView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Initialize views
        button = view.findViewById(R.id.button);
        urlEditText = view.findViewById(R.id.urleditText);
        imageButton2 = view.findViewById(R.id.imageButton2);

        // Set click listener for the button_scan
        View button_scan = view.findViewById(R.id.camerabutton1);
        button_scan.setOnClickListener(v -> scanCode());

        urlEditText = view.findViewById(R.id.urleditText);

        // 获取 Activity 的 Intent
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        if (data != null) {
            // 如果包含数据，将 URL 设置到 EditText 中
            String url = data.toString();
            urlEditText.setText(url);
        }

        return view;

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("請將相機對準QRcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), this::onActivityResult);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listener for imageButton2
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle onClick for imageButton2
            }
        });

        // Set click listener for other buttons
        view.findViewById(R.id.imageButton4).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentHome_to_blankFragment));
        view.findViewById(R.id.imageButton2).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentHome_to_fragmentdetail));

        // Set click listener for button
        button.setOnClickListener(v -> {
            // Get the URL from EditText
            String url = urlEditText.getText().toString();

            // 传递 URL 给 FragmentOutput
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            Navigation.findNavController(view).navigate(R.id.action_fragmentHome_to_fragmentOutput, bundle);
        });
    }

    private void sendDataTask(String url) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("140.132.102.244", 13245);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(url);
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e)  {
                e.printStackTrace();
            }
        }).start();
    }

    private void onActivityResult(ScanIntentResult result) {
        if (result.getContents() != null) {
            String scanResult = result.getContents();
            EditText urleditText = getView().findViewById(R.id.urleditText);
            if (urleditText !=null){
                urleditText.setText(scanResult);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
           /* builder.setTitle("Result");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show(); */
        }
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // 设置最大和最小缩放值
            scrollView.setScaleX(scaleFactor);
            scrollView.setScaleY(scaleFactor);
            return true;
        }
    }
    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        requireView().findViewById(R.id.drawer_layout).setBackgroundColor(backgroundColor);

        // 更新第二個ImageButton的背景顏色
        ImageButton imageButton2 = requireView().findViewById(R.id.camerabutton1);
        imageButton2.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        // 更新第三個ImageButton的背景顏色
        ImageButton imageButton3 = requireView().findViewById(R.id.imageButton4);
        imageButton3.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        ImageButton imageButton4 = requireView().findViewById(R.id.imageButton2);
        imageButton4.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (localeChanged()) {
            requireActivity().recreate();
        }
        applyTextSizeFromPreferences();
        updateBackgroundColor();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        updateLocale(context);
    }

    private void updateLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", MODE_PRIVATE);
        String language = prefs.getString("user_language", "en");
        String country = prefs.getString("user_country", "US");
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        // 检查是否是第一次运行应用，如果是，则默认语言设置为英文
        boolean isFirstRun = prefs.getBoolean("first_run", true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_language", "en");
            editor.putString("user_country", "US");
            editor.putBoolean("first_run", false);
            editor.apply();
            config.setLocale(new Locale("en", "US")); // 默认设置为英文
        }

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }


    private boolean localeChanged() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentLanguage = getResources().getConfiguration().getLocales().get(0).getLanguage();
        String savedLanguage = prefs.getString("user_language", currentLanguage);
        return !currentLanguage.equals(savedLanguage);
    }

    private void applyTextSizeFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(ChangeColorFragment.PREFS_NAME, MODE_PRIVATE);
        float textSize = sharedPreferences.getFloat(ChangeColorFragment.TEXT_SIZE_KEY, 18f);  // Default size

        Button buttonEnter = requireView().findViewById(R.id.button);
        EditText textView = requireView().findViewById(R.id.urleditText);
        buttonEnter.setTextSize(textSize);
        textView.setTextSize(textSize);
    }
    public void performAction() {
        // 这里是你希望自动点击的按钮的逻辑
        Button button = getView().findViewById(R.id.camerabutton1);
        button.performClick();  // 模拟按钮点击
    }



}

