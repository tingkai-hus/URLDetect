
package com.example.a1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

public class FragmentOutput extends Fragment {

    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private TextView textView1, textView2, textView3, textView4;
    private TextView safeProbability1, safeProbability2, safeProbability3, safeProbability4;
    private TextView Finaltext;
    private static final String SERVER_ADDRESS = "192.168.58.158";
    private static final int SERVER_PORT = 13245;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        return inflater.inflate(R.layout.fragment_output, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 找到TextView和安全概率的視圖
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        textView4 = view.findViewById(R.id.textView4);
        safeProbability1 = view.findViewById(R.id.safeprobability1);
        safeProbability2 = view.findViewById(R.id.safeprobability2);
        safeProbability3 = view.findViewById(R.id.safeprobability3);
        safeProbability4 = view.findViewById(R.id.safeprobability4);
        Finaltext = view.findViewById(R.id.Finaltext);

        // 發送請求並更新UI
        String url = getArguments().getString("url");
        connectToServer(url);
    }

    // 更新安全狀態
    private void setSafetyStatus(int modelNumber, String status) {
        switch (modelNumber) {
            case 1:
                textView1.setText(status);
                break;
            case 2:
                textView2.setText(status);
                break;
            case 3:
                textView3.setText(status);
                break;
            case 4:
                textView4.setText(status);
                break;
        }
    }

    // 更新安全機率
    private void setSafetyProbability(int modelNumber, String probability) {
        // 检查概率数据格式是否正确
        if (probability.contains(":")) {
            // 解析概率数据
            String[] parts = probability.split(":");
            if (parts.length > 1) {
                Double value = Double.valueOf(parts[1].trim()); // 获取概率值
                String safety = getString(R.string.safety);
                String danger = getString(R.string.danger);

                // 更新UI
                switch (modelNumber) {

                    case 1:
                        safeProbability1.setText(safety + value + "%");
                        break;
                    case 2:
                        safeProbability2.setText(safety + value + "%");
                        break;
                    case 3:
                        safeProbability3.setText(safety + value + "%");
                        break;
                    case 4:
                        safeProbability4.setText(safety + value + "%");
                        break;
                }
            } else {
                // 输出日志，显示概率数据格式不正确
                Log.e("FragmentOutput", "Invalid probability format: " + probability);
            }
        } else {
            // 输出日志，显示概率数据格式不正确
            Log.e("FragmentOutput", "Invalid probability format: " + probability);
        }
    }

    // 發送請求到伺服器
    private void connectToServer(String url) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(url);

                // 读取服务器响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                String serverResponse = responseBuilder.toString();

                Log.d("FragmentOutput", "Received data from server: " + serverResponse);

                // 关闭 socket
                socket.close();

                // 更新 UI
                getActivity().runOnUiThread(() -> {
                    parseServerResponse(serverResponse);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseServerResponse(String serverResponse) {
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONObject details = jsonObject.getJSONObject("details");
            String result =jsonObject.getString("result");
            Finaltext.setText(result);
            if (result.equals("安全的URL")) {
                Finaltext.setText(R.string.safe_url);
                Finaltext.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorSafe));
            } else if (result.equals("危險的URL")) {
                Finaltext.setText(R.string.dangerous_url);
                Finaltext.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorDanger));
            } else {
                Finaltext.setText(R.string.unknown_status);
            }

            // 提取 CNN 模型的概率值
            if (details.has("CNN\u6aa2\u6e2c\u51fa \u5b89\u5168\u7684 URL")) {
                String cnnDetails = details.getString("CNN\u6aa2\u6e2c\u51fa \u5b89\u5168\u7684 URL");
                setSafetyProbability(1, cnnDetails);
            }
            // 提取 RF 模型的概率值
            if (details.has("RF\u6aa2\u6e2c\u51fa \u5b89\u5168\u7684 URL")) {
                String rfDetails = details.getString("RF\u6aa2\u6e2c\u51fa \u5b89\u5168\u7684 URL");
                setSafetyProbability(2, rfDetails);
            }

            // 提取 RNN 模型的概率值
            if (details.has("RNN\u6AA2\u6E2C\u51FA \u5B89\u5168\u7684 URL")) {
                String rnnDetails = details.getString("RNN\u6AA2\u6E2C\u51FA \u5B89\u5168\u7684 URL");
                setSafetyProbability(3, rnnDetails);
            }
            // 提取 SVM 模型的概率值
            if (details.has("SVM\u6aa2\u6e2c\u51fa \u5B89\u5168\u7684 URL")) {
                String svmDetails = details.getString("SVM\u6aa2\u6e2c\u51fa \u5b89\u5168\u7684 URL");
                setSafetyProbability(4, svmDetails);
            }
            if (details.has("CNN\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL")) {
                String cnnDangerDetails = details.getString("CNN\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL");
                setSafetyProbability(1, cnnDangerDetails);
            }

            if (details.has("RF\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL")) {
                String rfDangerDetails = details.getString("RF\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL");
                setSafetyProbability(2, rfDangerDetails);
            }

            if (details.has("RNN\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL")) {
                String rnnDangerDetails = details.getString("RNN\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL");
                setSafetyProbability(3, rnnDangerDetails);
            }

            if (details.has("SVM\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL")) {
                String svmDangerDetails = details.getString("SVM\u6aa2\u6e2c\u51fa \u5371\u96aa\u7684 URL");
                setSafetyProbability(4, svmDangerDetails);
            }

            if (details != null) {
                String safeUrlDetected = getString(R.string.det_safe);
                String statusSafe = getString(R.string.Safe);
                String dangerousUrlDetected = getString(R.string.det_dangerous);
                String statusgangerous = getString(R.string.dangerous);
                String[] modelLabels = {"CNN", "RF", "RNN", "SVM"};
                for (int i = 0; i < modelLabels.length; i++) {
                    String modelName = modelLabels[i];
                    if (details.has(modelName + safeUrlDetected)) {
                        setSafetyStatus(i + 1, statusSafe);
                    } else if (details.has(modelName + dangerousUrlDetected)) {
                        setSafetyStatus(i + 1, statusgangerous);
                    }
                }
            }
            if (details != null) {
                String dangerous = getString(R.string.todanger);
                String safe = getString(R.string.tosafe);
                String[] modelLabels = {"CNN", "RF", "RNN", "SVM"};
                for (int i = 0; i < modelLabels.length; i++) {
                    String modelName = modelLabels[i];
                    if (details.has(modelName + "檢測出 安全的 URL")) {
                        setSafetyStatus(i + 1, safe);
                    } else if (details.has(modelName + "檢測出 危險的 URL")) {
                        setSafetyStatus(i + 1, dangerous);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateBackgroundColor() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("appPreferences", MODE_PRIVATE);
        int defaultBackgroundColor = ContextCompat.getColor(requireContext(), R.color.背景);
        int backgroundColor = prefs.getInt("background_color", defaultBackgroundColor);
        requireView().findViewById(R.id.Layout).setBackgroundColor(backgroundColor);
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

        /*
        TextView textView11 = requireView().findViewById(R.id.safeprobability4);
        TextView textView10 = requireView().findViewById(R.id.safeprobability2);
        TextView textView = requireView().findViewById(R.id.safeprobability3);
        TextView textView1 = requireView().findViewById(R.id.textView1);
        TextView textView2 = requireView().findViewById(R.id.textView2);
        TextView textView3 = requireView().findViewById(R.id.textView3);
        TextView textView4 = requireView().findViewById(R.id.textView4);
        TextView textView5 = requireView().findViewById(R.id.safeprobability1);
        textView.setTextSize(textSize);
        textView1.setTextSize(textSize);
        textView2.setTextSize(textSize);
        textView3.setTextSize(textSize);
        textView4.setTextSize(textSize);
        textView5.setTextSize(textSize);
        textView10.setTextSize(textSize);
        textView11.setTextSize(textSize);*/
    }


}
