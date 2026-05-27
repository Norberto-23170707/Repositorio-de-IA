import cv2
import numpy as np
import tensorflow as tf
import os

# 1. Cargar el modelo que acabas de entrenar
print("Cargando el cerebro de la IA...")
model = tf.keras.models.load_model('modelo_reconocimiento_facial.keras')

# 2. Obtener los nombres de las clases (las carpetas)
# Keras las ordena alfabéticamente por defecto
ruta_entrenamiento = os.path.join('Dataset_Final', 'train')
nombres_clases = sorted(os.listdir(ruta_entrenamiento))
print(f"Clases detectadas: {nombres_clases}")

# 3. Cargar el detector de rostros (Haar Cascade)
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

# 4. Iniciar la cámara
cap = cv2.VideoCapture(0)
print("Cámara iniciada. Presiona 'q' para salir.")

while True:
    ret, frame = cap.read()
    if not ret:
        break

    # Convertir a escala de grises para que el Haar Cascade detecte el rostro
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    rostros = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(100, 100))

    for (x, y, w, h) in rostros:
        # Dibujar un rectángulo alrededor del rostro detectado
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)

        # Recortar el rostro del frame original
        rostro_recortado = frame[y:y+h, x:x+w]

        # Preprocesar el rostro para pasárselo al modelo
        rostro_redimensionado = cv2.resize(rostro_recortado, (160, 160)) # Mismo tamaño que en el entrenamiento
        rostro_rgb = cv2.cvtColor(rostro_redimensionado, cv2.COLOR_BGR2RGB) # Keras usa RGB, OpenCV usa BGR
        rostro_array = np.expand_dims(rostro_rgb, axis=0) # Añadir dimensión de lote: (1, 160, 160, 3)

        # 5. Hacer la predicción
        predicciones = model.predict(rostro_array, verbose=0)
        indice_clase = np.argmax(predicciones[0])
        porcentaje_confianza = predicciones[0][indice_clase] * 100

        # Obtener el nombre de la persona
        nombre_predicho = nombres_clases[indice_clase]

        # 6. Mostrar el resultado en pantalla
        texto = f"{nombre_predicho} ({porcentaje_confianza:.1f}%)"
        
        # Si la confianza es muy baja, podríamos decir que es "Desconocido"
        if porcentaje_confianza < 60:
            texto = "Desconocido"
            cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 0, 255), 2) # Rectángulo rojo si no está seguro

        cv2.putText(frame, texto, (x, y-10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

    # Mostrar la ventana
    cv2.imshow('Prueba de Reconocimiento Facial', frame)

    # Salir con la tecla 'q'
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()