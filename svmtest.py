from sklearn import svm
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import csv

file_path = "/Users/xutingkai/Desktop/lmplementation/newhardworking/small.csv"
data = pd.read_csv(file_path)
df = pd.read_csv(file_path,)

X = df.drop(columns=['phishing'])   # 去除 'phishing' 列作為特徵
y = df['phishing']  # 假設 'phishing' 是你的目標變量

X_train, X_test, y_train, y_test = train_test_split(X,y,test_size=0.2,random_state=101)


clf=svm.SVC(kernel='linear',C=0.1)
#print("開始訓練模型...")

clf.fit(X_train,y_train)
print("模型训练完成")

y_pred = clf.predict(X_test)

print("支援向量的數量:", len(clf.support_vectors_))

accuracy = accuracy_score(y_test, y_pred)
print(f"Accuracy: {accuracy}")

#error_bad_lines=False ,C=1,max_iter=-1