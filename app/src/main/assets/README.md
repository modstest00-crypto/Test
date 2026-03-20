# TensorFlow Lite Model for SnapSort

This directory should contain the TensorFlow Lite model file (`model.tflite`) for image classification.

## Model Requirements

- **Input**: 224x224 RGB image
- **Output**: 12-class classification (matching labels.txt)
- **Format**: TensorFlow Lite (.tflite)

## Recommended Models

For production use, you can use one of these pre-trained models:

1. **MobileNetV2** - Lightweight and efficient
   - Download from: https://tfhub.dev/google/tf2-preview/mobilenet_v2/classification/4
   - Convert to TFLite format

2. **EfficientNet-Lite** - Better accuracy
   - Download from: https://tfhub.dev/tensorflow/lite-model/efficientnet/lite0/classification/2

3. **Custom Trained Model** - Train on screenshot dataset
   - Collect screenshot samples for each category
   - Train using TensorFlow
   - Convert to TFLite using TFLiteConverter

## Quick Setup (Development)

For development/testing, you can:

1. Download a pre-trained MobileNetV2 TFLite model
2. Rename it to `model.tflite`
3. Place it in this `assets` directory
4. Update the `labels.txt` to match the model's classes

## Model Training Script (Example)

```python
import tensorflow as tf

# Load pre-trained model
base_model = tf.keras.applications.MobileNetV2(
    input_shape=(224, 224, 3),
    include_top=False,
    weights='imagenet'
)

# Add custom classification head for screenshot categories
model = tf.keras.Sequential([
    base_model,
    tf.keras.layers.GlobalAveragePooling2D(),
    tf.keras.layers.Dropout(0.2),
    tf.keras.layers.Dense(12, activation='softmax')  # 12 categories
])

# Compile and train
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save
with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
```

## Note

The app will work without the model file, but classification will default to "other" category.
For full functionality, please add a proper TFLite model.
