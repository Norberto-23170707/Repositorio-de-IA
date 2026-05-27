import cv2
import os
import glob

# 1. Configuración
# Pon aquí el nombre de la carpeta del actor que quieres procesar
nombre_actor = 'Tom Cruise'  # Cambia esto por el nombre de tu carpeta
path_origen = os.path.join('dataset', nombre_actor)
path_destino = os.path.join('dataset_procesado', nombre_actor) # Guardaremos las buenas aquí

if not os.path.exists(path_destino):
    os.makedirs(path_destino)

face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

print(f"Procesando imágenes de {nombre_actor}...")
count = 0

# 2. Leer todas las imágenes .jpg o .png de la carpeta del actor
rutas_imagenes = glob.glob(os.path.join(path_origen, '*.*'))

for img_path in rutas_imagenes:
    img = cv2.imread(img_path)
    if img is None: continue # Saltar si el archivo no es una imagen válida
    
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)

    # 3. Recortar y guardar
    for (x, y, w, h) in faces:
        rostro_recortado = img[y:y + h, x:x + w]
        rostro_recortado = cv2.resize(rostro_recortado, (60, 60), interpolation=cv2.INTER_CUBIC)
        
        # Guardar con el mismo formato que usaste para las tuyas
        cv2.imwrite(os.path.join(path_destino, f'rostro_{count}.jpg'), rostro_recortado)
        count += 1

print(f"¡Listo! Se extrajeron {count} rostros limpios en la carpeta {path_destino}")