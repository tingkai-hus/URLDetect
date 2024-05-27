'''
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import pandas as pd
import joblib

# 指定數據文件的路徑
file_path = "/Users/xutingkai/Desktop/lmplementation/newhardworking/superbignew.csv"  
df = pd.read_csv(file_path)
X = df.drop(columns=['phishing'])  # 去除 'phishing' 列作為特徵
y = df['phishing']  # 'phishing' 列是目標

# 將數據集分成訓練集和測試集
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# 創建隨機森林分類器，注意設定 class_weight='balanced'
clf = RandomForestClassifier(n_estimators=500, random_state=42, class_weight='balanced')  
clf.fit(X_train, y_train)

# 保存模型到文件
model_filename = "/Users/xutingkai/Desktop/lmplementation/newhardworking/RFfinish.pkl"
joblib.dump(clf, model_filename)
'''

#SVM
from sklearn import svm
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

file_path = "/Users/xutingkai/Desktop/lmplementation/newhardworking/superbignew.csv"
data = pd.read_csv(file_path)
df = pd.read_csv(file_path,)

X = df.drop(columns=['phishing'])
y = df['phishing']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=101)

# 定義分批訓練的批次大小
batch_size = 1000

# 獲取訓練數據的總數
total_samples = len(X_train)

# 初始化模型
svm_model = svm.SVC(kernel='linear', C=0.1)

# 迭代進行多次訓練
for epoch in range(10):  # 假設進行 10 次訓練
    for start in range(0, total_samples, batch_size):
        end = min(start + batch_size, total_samples)
        
        # 提取本次批次的數據
        X_batch = X_train.iloc[start:end]
        y_batch = y_train.iloc[start:end]
        
        # 訓練模型
        svm_model.fit(X_batch, y_batch)

# 最後，你可以使用測試集評估模型的性能
y_pred = svm_model.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
print(f"模型在測試集上的準確率：{accuracy}")


from sklearn.model_selection import GridSearchCV
from sklearn import svm

# 定義參數範圍
param_grid = {
    'C': [0.1, 1, 10, 100],  # 示例值
    'gamma': [1, 0.1, 0.01, 0.001],  # 示例值
    'kernel': ['rbf', 'linear']  # 你可以加入更多核函數來測試
}

# 創建 GridSearchCV 對象
grid = GridSearchCV(svm.SVC(), param_grid, refit=True, verbose=2, cv=5)

# 在訓練集上執行網格搜索
grid.fit(X_train, y_train)

# 打印最佳參數和在測試集上的表現
print("最佳參數：", grid.best_params_)
print("最佳準確率：", grid.score(X_test, y_test))


