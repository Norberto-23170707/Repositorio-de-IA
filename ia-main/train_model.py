import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.applications import MobileNetV2
import os

# 1. Configuración de parámetros
IMG_SIZE = (160, 160)
BATCH_SIZE = 32
DATA_DIR = 'Dataset_Final'

# Determinar número de clases automáticamente contando las carpetas
NUM_CLASSES = len(os.listdir(os.path.join(DATA_DIR, 'train')))

# 2. Carga de Datos
print("Cargando datos de entrenamiento...")
train_dataset = tf.keras.utils.image_dataset_from_directory(
    os.path.join(DATA_DIR, 'train'),
    shuffle=True,
    batch_size=BATCH_SIZE,
    image_size=IMG_SIZE)

print("Cargando datos de validación...")
validation_dataset = tf.keras.utils.image_dataset_from_directory(
    os.path.join(DATA_DIR, 'val'),
    shuffle=True,
    batch_size=BATCH_SIZE,
    image_size=IMG_SIZE)

# Optimización de carga en memoria
AUTOTUNE = tf.data.AUTOTUNE
train_dataset = train_dataset.prefetch(buffer_size=AUTOTUNE)
validation_dataset = validation_dataset.prefetch(buffer_size=AUTOTUNE)

# 3. Aumentación de Datos (Tal como solicitaste en tu metodología)
data_augmentation = tf.keras.Sequential([
    layers.RandomFlip("horizontal"),
    layers.RandomRotation(0.1), # Rotación del 10%
    layers.RandomBrightness(0.2), # Variación de brillo
])

# 4. Arquitectura del Modelo (Transfer Learning)
# Usamos MobileNetV2 pre-entrenado, excluyendo la última capa
base_model = MobileNetV2(input_shape=IMG_SIZE + (3,), 
                         include_top=False, 
                         weights='imagenet')

# Congelamos la base para no destruir los pesos pre-entrenados
base_model.trainable = False

# Construimos nuestro modelo multiclase
model = models.Sequential([
    layers.InputLayer(input_shape=IMG_SIZE + (3,)),
    data_augmentation,          # Aumentación aplicada SOLO en entrenamiento
    layers.Rescaling(1./127.5, offset=-1), # Normalización de píxeles para MobileNetV2 [-1, 1]
    base_model,                 # Extractor de características
    layers.GlobalAveragePooling2D(),
    layers.Dense(128, activation='relu'),
    layers.Dropout(0.5),        # Previene sobreajuste
    layers.Dense(NUM_CLASSES, activation='softmax') # Clasificación Multiclase
])

# 5. Compilación del Modelo
model.compile(optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
              loss=tf.keras.losses.SparseCategoricalCrossentropy(),
              metrics=['accuracy'])

model.summary()

# 6. Entrenamiento
print("\nIniciando el entrenamiento del modelo...")
EPOCHS = 15
history = model.fit(
    train_dataset,
    validation_data=validation_dataset,
    epochs=EPOCHS
)

# 7. Guardar el Modelo Entrenado
model.save('modelo_reconocimiento_facial.keras')
print("Entrenamiento finalizado y modelo guardado como 'modelo_reconocimiento_facial.keras'.")