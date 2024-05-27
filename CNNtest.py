
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt

# 讀取 CSV 檔案
data = pd.read_csv('/Users/xutingkai/Desktop/lmplementation/newhardworking/superbignew.csv')  

# 提取特徵和標籤
features = data.iloc[:, :-1]  # 提取所有列除了最後一列作為特徵
labels = data.iloc[:, -1]  # 提取最後一列作為標籤

train_features, test_features, train_labels, test_labels = train_test_split(features, labels, test_size=0.2, random_state=42)

# 定義卷積神經網絡模型
model = tf.keras.Sequential([
    tf.keras.layers.Dense(32, activation='relu', input_shape=(len(features.columns),)),
    tf.keras.layers.Dense(16, activation='relu'),
    tf.keras.layers.Dense(1, activation='sigmoid')
])


# 編譯模型
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# 訓練模型
history = model.fit(train_features, train_labels, epochs=10, batch_size=32, validation_data=(test_features, test_labels))

# 評估模型
test_loss, test_accuracy = model.evaluate(test_features, test_labels)
print(f"Test Accuracy: {test_accuracy}")

model.save("/Users/xutingkai/Desktop/lmplementation/newhardworking/CNNfinish.h5")
'''
# 獲取訓練過程中的數據
train_loss = history.history['loss']
val_loss = history.history['val_loss']
train_acc = history.history['accuracy']
val_acc = history.history['val_accuracy']

# 繪製訓練和驗證集的損失曲線
epochs = range(1, len(train_loss) + 1)

plt.plot(epochs, train_loss, 'g', label='Training loss')
plt.plot(epochs, val_loss, 'b', label='Validation loss')
plt.title('Training and Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.show()

# 繪製訓練和驗證集的準確率曲線
plt.plot(epochs, train_acc, 'g', label='Training accuracy')
plt.plot(epochs, val_acc, 'b', label='Validation accuracy')
plt.title('Training and Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()
plt.show()
'''
'''
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split

# 讀取 CSV 檔案
data = pd.read_csv('/Users/xutingkai/Desktop/lmplementation/newhardworking/superbignew.csv')

# 提取特徵和標籤
features = data.iloc[:, :-1]  # 提取所有列除了最後一列作為特徵
labels = data.iloc[:, -1]  # 提取最後一列作為標籤

# 將特徵按照時間步長轉換為序列
time_steps = len(features.columns)
features = features.values.reshape(-1, time_steps, 1)

# 分割數據集
train_features, test_features, train_labels, test_labels = train_test_split(features, labels, test_size=0.2, random_state=42)

# 定義 RNN 模型
model = tf.keras.Sequential([
    tf.keras.layers.LSTM(32, activation='relu', input_shape=(time_steps, 1)),
    tf.keras.layers.Dense(1, activation='sigmoid')
])

optimizer = tf.keras.optimizers.Adam(learning_rate=0.001)  # 調整學習率

# 編譯模型
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# 訓練模型
history = model.fit(train_features, train_labels, epochs=10, batch_size=32, validation_data=(test_features, test_labels))

# 評估模型
test_loss, test_accuracy = model.evaluate(test_features, test_labels)
print(f"Test Accuracy: {test_accuracy}")

# 保存模型
model.save("/Users/xutingkai/Desktop/lmplementation/newhardworking/RNNfinish.h5")
'''