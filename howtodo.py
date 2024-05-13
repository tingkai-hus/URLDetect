import socket
import json
import os
import pandas as pd
from urllib.parse import urlparse
import joblib
import numpy as np
from keras.models import load_model
import pickle


class CustomJSONEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()  # 将 NumPy 数组转换为列表
        return super().default(obj)


def extract_url_attributes(url):
    # 解析URL
    parsed_url = urlparse(url)

    # 獲取網址中所有字母和指定標點符號
    all_characters = parsed_url.netloc + parsed_url.path + parsed_url.query

    # 定義需要計算的標點符號
    specified_symbols = ".-_/=?@&!~+,*$#% "

    # 初始化標點符號數量的字典
    symbol_counts = {symbol: 0 for symbol in specified_symbols}

    # 初始化字母數量
    letter_count = 0

    # 遍歷網址，計算字母數量和標點符號數量
    for char in all_characters:
        if char.isalpha():
            # 如果是字母，增加字母數量
            letter_count += 1
        elif char in specified_symbols:
            # 如果是指定的標點符號，增加對應符號的數量
            symbol_counts[char] += 1

    # 檢查是否有email存在於網址中
    # email_present = "@" in all_characters

    # 定義提取標點符號的函數
    def extract_punctuation(text):
        symbols = ".-_/?=@&!~,+*#$% "
        return [char for char in text if char in symbols]

    # 返回字母數量、各個指定標點符號的數量
    result_url = {
        'qty_dot_url': symbol_counts['.'],
        'qty_hyphen_url': symbol_counts['-'],
        'qty_underline_url': symbol_counts['_'],
        'qty_slash_url': symbol_counts['/'],
        'qty_questionmark_url': symbol_counts['?'],
        'qty_equal_url': symbol_counts['='],
        'qty_at_url': symbol_counts['@'],
        'qty_and_url': symbol_counts['&'],
        'qty_exclamation_url': symbol_counts['!'],
        'qty_space_url': symbol_counts[' '],
        'qty_tilde_url': symbol_counts['~'],
        'qty_comma_url': symbol_counts[','],
        'qty_plus_url': symbol_counts['+'],
        'qty_asterisk_url': symbol_counts['*'],
        'qty_hashtag_url': symbol_counts['#'],
        'qty_dollar_url': symbol_counts['$'],
        'qty_percent_url': symbol_counts['%'],
        'qty_tld_url': len(extract_punctuation(parsed_url.netloc)),
        'length_url': letter_count,
        # 'email_in_url': email_present
    }

    # 返回結果
    return result_url


def count_characters_and_punctuationfordomain(url):
    # 解析URL
    parsed_url = urlparse(url)
    # 獲取域名部分
    domain = parsed_url.netloc
    # 定義需要計算的標點符號
    specified_symbols = ",/-=?@&!~_#+%$* "
    # 初始化標點符號數量的字典
    symbol_counts = {symbol: 0 for symbol in specified_symbols}
    # 初始化字母數量
    letter_count = 0
    # 遍歷域名部分，計算字母數量和標點符號數量
    for char in domain:
        if char.isalpha():
            # 如果是字母，增加字母數量
            letter_count += 1
        elif char in specified_symbols:
            # 如果是指定的標點符號，增加對應符號的數量
            symbol_counts[char] += 1

    # 返回字母數量和各個指定標點符號的數量
    result_domain = {
        'qty_dot_domain': symbol_counts.get('.', 0),
        'qty_hyphen_domain': symbol_counts.get('-', 0),
        'qty_underline_domain': symbol_counts.get('_', 0),
        'qty_slash_domain': symbol_counts.get('/', 0),
        'qty_questionmark_domain': symbol_counts.get('?', 0),
        'qty_equal_domain': symbol_counts.get('=', 0),
        'qty_at_domain': symbol_counts.get('@', 0),
        'qty_and_domain': symbol_counts.get('&', 0),
        'qty_exclamation_domain': symbol_counts.get('!', 0),
        'qty_space_domain': symbol_counts.get(' ', 0),
        'qty_tilde_domain': symbol_counts.get('~', 0),
        'qty_comma_domain': symbol_counts.get(',', 0),
        'qty_plus_domain': symbol_counts.get('+', 0),
        'qty_asterisk_domain': symbol_counts.get('*', 0),
        'qty_hashtag_domain': symbol_counts.get('#', 0),
        'qty_dollar_domain': symbol_counts.get('$', 0),
        'qty_percent_domain': symbol_counts.get('%', 0),
        'qty_vowels_domain': len([char for char in domain if char.lower() in 'aeiou']),
        'domain_length': len(domain),
        'domain_in_ip': -1,  # Placeholder for actual implementation
        'server_client_domain': -1  # Placeholder for actual implementation
    }

    return result_domain


def count_characters_and_punctuationfordir(url):
    # 解析URL
    parsed_url = urlparse(url)
    # 獲取目錄部分
    directory = parsed_url.path
    # 定義需要計算的標點符號
    specified_symbols = ",/-=?@&!~_#+%$* "
    # 初始化標點符號數量的字典
    symbol_counts = {symbol: 0 for symbol in specified_symbols}
    # 初始化字母數量
    letter_count = 0
    # 遍歷目錄部分，計算字母數量和標點符號數量
    for char in directory:
        if char.isalpha():
            # 如果是字母，增加字母數量
            letter_count += 1
        elif char in specified_symbols:
            # 如果是指定的標點符號，增加對應符號的數量
            symbol_counts[char] += 1

    # 返回字母數量和各個指定標點符號的數量
    result_dir = {
        'qty_dot_directory': symbol_counts.get('.', 0),
        'qty_hyphen_directory': symbol_counts.get('-', 0),
        'qty_underline_directory': symbol_counts.get('_', 0),
        'qty_slash_directory': symbol_counts.get('/', 0),
        'qty_questionmark_directory': symbol_counts.get('?', 0),
        'qty_equal_directory': symbol_counts.get('=', 0),
        'qty_at_directory': symbol_counts.get('@', 0),
        'qty_and_directory': symbol_counts.get('&', 0),
        'qty_exclamation_directory': symbol_counts.get('!', 0),
        'qty_space_directory': symbol_counts.get(' ', 0),
        'qty_tilde_directory': symbol_counts.get('~', 0),
        'qty_comma_directory': symbol_counts.get(',', 0),
        'qty_plus_directory': symbol_counts.get('+', 0),
        'qty_asterisk_directory': symbol_counts.get('*', 0),
        'qty_hashtag_directory': symbol_counts.get('#', 0),
        'qty_dollar_directory': symbol_counts.get('$', 0),
        'qty_percent_directory': symbol_counts.get('%', 0),
        'directory_length': len(directory)
    }

    return result_dir


def count_characters_and_punctuationfime(url):
    # 解析URL
    parsed_url = urlparse(url)
    # 獲取文件名
    file_name = os.path.basename(parsed_url.path)
    # 定義需要計算的標點符號
    specified_symbols = ",/-=?@&!~_#+%$* "
    # 初始化標點符號數量的字典
    symbol_counts = {symbol: 0 for symbol in specified_symbols}
    # 初始化字母數量
    letter_count = 0
    # 遍歷文件名，計算字母數量和標點符號數量
    for char in file_name:
        if char.isalpha():
            # 如果是字母，增加字母數量
            letter_count += 1
        elif char in specified_symbols:
            # 如果是指定的標點符號，增加對應符號的數量
            symbol_counts[char] += 1

    # 返回字母數量和各個指定標點符號的數量
    result_fime = {
        'qty_dot_filename': symbol_counts.get('.', 0),
        'qty_hyphen_filename': symbol_counts.get('-', 0),
        'qty_underline_filename': symbol_counts.get('_', 0),
        'qty_slash_filename': symbol_counts.get('/', 0),
        'qty_questionmark_filename': symbol_counts.get('?', 0),
        'qty_equal_filename': symbol_counts.get('=', 0),
        'qty_at_filename': symbol_counts.get('@', 0),
        'qty_and_filename': symbol_counts.get('&', 0),
        'qty_exclamation_filename': symbol_counts.get('!', 0),
        'qty_space_filename': symbol_counts.get(' ', 0),
        'qty_tilde_filename': symbol_counts.get('~', 0),
        'qty_comma_filename': symbol_counts.get(',', 0),
        'qty_plus_filename': symbol_counts.get('+', 0),
        'qty_asterisk_filename': symbol_counts.get('*', 0),
        'qty_hashtag_filename': symbol_counts.get('#', 0),
        'qty_dollar_filename': symbol_counts.get('$', 0),
        'qty_percent_filename': symbol_counts.get('%', 0),
        'filename_length': len(file_name)
    }

    return result_fime


def count_characters_and_punctuationforurlpar(url):
    # 解析URL
    parsed_url = urlparse(url)
    # 獲取參數部分
    parameters = parsed_url.query
    # 定義需要計算的標點符號
    specified_symbols = ",/-=?@&!~_#+%$* "
    # 初始化標點符號數量的字典
    symbol_counts = {symbol: 0 for symbol in specified_symbols}
    # 初始化字母數量
    letter_count = 0
    # 遍歷參數部分，計算字母數量和標點符號數量
    for char in parameters:
        if char.isalpha():
            # 如果是字母，增加字母數量
            letter_count += 1
        elif char in specified_symbols:
            # 如果是指定的標點符號，增加對應符號的數量
            symbol_counts[char] += 1

    # 返回字母數量和各個指定標點符號的數量
    result_urlpar = {
        'qty_dot_parameters': symbol_counts.get('.', 0),
        'qty_hyphen_parameters': symbol_counts.get('-', 0),
        'qty_underline_parameters': symbol_counts.get('_', 0),
        'qty_slash_parameters': symbol_counts.get('/', 0),
        'qty_questionmark_parameters': symbol_counts.get('?', 0),
        'qty_equal_parameters': symbol_counts.get('=', 0),
        'qty_at_parameters': symbol_counts.get('@', 0),
        'qty_and_parameters': symbol_counts.get('&', 0),
        'qty_exclamation_parameters': symbol_counts.get('!', 0),
        'qty_space_parameters': symbol_counts.get(' ', 0),
        'qty_tilde_parameters': symbol_counts.get('~', 0),
        'qty_comma_parameters': symbol_counts.get(',', 0),
        'qty_plus_parameters': symbol_counts.get('+', 0),
        'qty_asterisk_parameters': symbol_counts.get('*', 0),
        'qty_hashtag_parameters': symbol_counts.get('#', 0),
        'qty_dollar_parameters': symbol_counts.get('$', 0),
        'qty_percent_parameters': symbol_counts.get('%', 0),
        'parameters_length': len(parameters)
    }

    return result_urlpar


# 主函数
def process_url_data(url):
    # 调用各个函数，处理URL数据
    result_url = extract_url_attributes(url)
    result_domain = count_characters_and_punctuationfordomain(url)
    result_dir = count_characters_and_punctuationfordir(url)
    result_fime = count_characters_and_punctuationfime(url)
    result_urlpar = count_characters_and_punctuationforurlpar(url)

    # 处理结果，将 None 替换为 0
    result_url = {key: 0 if value is None else value for key,
                  value in result_url.items()}
    result_domain = {key: 0 if value is None else value for key,
                     value in result_domain.items()}
    result_dir = {key: 0 if value is None else value for key,
                  value in result_dir.items()}
    result_fime = {key: 0 if value is None else value for key,
                   value in result_fime.items()}
    result_urlpar = {key: 0 if value is None else value for key,
                     value in result_urlpar.items()}

# 合并结果
    result_combined = {**result_url, **result_domain,
                       **result_dir, **result_fime, **result_urlpar}

    return result_combined

    # 載入模型路徑


def input_model(user_input, models):
    # 初始化一个变量，用于记录是否有任何一个模型判断为釣魚 URL
    any_model_detected_phishing = False
    detailed_results = []

    # 遍历所有模型
    for model_path in models:
        # 加載模型
        if model_path.endswith(".h5"):  # 如果是 Keras 模型
            model = load_model(model_path)
            # 构造一个特徵值的字典（这裡的键和順序應与模型期望的一致）
            sample_features = process_url_data(user_input)
            # 将特徵值转换為 Numpy 陣列
            features_array = np.array([list(sample_features.values())])
            # 使用模型进行預測
            predictions = model.predict(features_array)
        elif model_path.endswith(".pkl"):  # 如果是 Scikit-Learn 模型
            with open(model_path, "rb") as file:
                model = pickle.load(file)
            # 构造一个特徵值的字典（这裡的键和順序應与模型期望的一致）
            sample_features = process_url_data(user_input)
            # 将特徵值转换為 Numpy 陣列
            features_array = np.array([list(sample_features.values())])
            # 使用模型进行預測
            if hasattr(model, "predict_proba"):
                predictions = model.predict_proba(features_array)[:, 1]
            else:
                # 如果模型没有 predict_proba 方法，可以進行相應的處理
                print(
                    f"Model {model_path} does not have predict_proba method.")
                continue
        else:
            # 如果模型格式不受支持，可以進行相應的處理
            print(f"Unsupported model format: {model_path}")
            continue

        # 添加詳細結果
        detailed_results.append({'prediction': predictions[0].astype(float)})

        # 如果任何一个模型判断为釣魚 URL，则更新标志变量，并跳出循环
        if predictions > 0.5:
            any_model_detected_phishing = True
            break

    # 根據預測結果判斷是不是釣魚 URL
    result = "這是釣魚 URL" if any_model_detected_phishing else "這是正常 URL"

    # 直接返回結果和詳細結果，不進行 print 操作
    return result, detailed_results


# 創建伺服器套接字
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
host = '192.168.21.227'  # 將這裡替換為你電腦的本地IP地址
port = 13245  # 可以自選一個端口號

# 綁定伺服器套接字到指定地址和端口
server_socket.bind((host, port))

# 設置監聽
server_socket.listen(1)
print(f"伺服器正在監聽 {host}:{port} ...")

while True:
    # 等待客戶端連接
    client_socket, client_address = server_socket.accept()
    print(f"接收來自 {client_address} 的連接")

    try:
        received_data = client_socket.recv(1024)
        if isinstance(received_data, bytes):
            url_from_phone = received_data.decode('utf-8')
        else:
            print("Received data is not in bytes format.")
            continue

        print(f"接收到來自手機的URL: {url_from_phone}")

        # 處理URL數據
        url_features = process_url_data(url_from_phone)
        print(url_features)
        model_paths = [
            "CNNfinish.h5",
            "RFfinish.pkl",
            "RNNfinish.h5",
            "SVMfinish.pkl"
        ]
        # 調用 input_model 函數進行模型預測
        result = input_model(url_from_phone, model_paths)

        # 直接显示模型预测结果
        result_text.config(state=tk.NORMAL)
        result_text.delete(1.0, tk.END)
        result_text.insert(tk.END, str(result))
        result_text.config(state=tk.DISABLED)

        print(f"成功")
        print(result)

        # 将结果打包成 JSON 格式，只包含 result
        result_json = json.dumps({'result': result}, cls=CustomJSONEncoder)

        # 发送结果给手机
        client_socket.send(result_json.encode('utf-8'))
        print("结果已发送至手机")

    except Exception as e:
        print(f"发生错误: {e}")

    finally:
        # 关闭与客户端的连接
        client_socket.close()
