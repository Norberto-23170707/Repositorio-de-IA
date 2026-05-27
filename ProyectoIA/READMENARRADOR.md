# Narrador de Video con IA Multimodal

Sistema de inteligencia artificial que analiza clips de video y genera narración automática en español combinando modelos de Visión, Lenguaje y Audio.

---

## Arquitectura del Sistema

```
Video (.mp4)
     │
     ▼
┌─────────────────┐
│  Módulo #1      │  OpenCV — Captura frames cada N segundos
│  CAPTURA        │
└────────┬────────┘
         │ frames (PIL Images)
         ▼
┌─────────────────┐
│  Módulo #5      │  Heurística de diferencia de píxeles
│  CONTROL        │  → Omite frames sin cambio significativo
└────────┬────────┘
         │ frame relevante
         ▼
┌─────────────────┐
│  Módulo #2      │  BLIP (Hugging Face)
│  VISIÓN         │  → Descripción en inglés del frame
└────────┬────────┘
         │ descripción visual
         ▼
┌─────────────────┐
│  Módulo #3      │  Helsinki NLP + GPT-2 (Hugging Face)
│  NARRACIÓN NLP  │  → Traducción + comentario creativo en español
└────────┬────────┘
         │ comentario
         ▼
┌─────────────────┐
│  Módulo #4      │  MMS-TTS (Hugging Face)
│  VOZ / TTS      │  → Archivo .wav con la narración
└────────┬────────┘
         │ audio clips
         ▼
┌─────────────────┐
│  ENSAMBLADOR    │  MoviePy — Mezcla audio sobre el video original
│  DE VIDEO       │  (video original silenciado)
└────────┬────────┘
         │
         ▼
   video_narrado.mp4
```

---

## Modelos de Hugging Face Utilizados

| Módulo | Modelo | Tarea |
|--------|--------|-------|
| Visión (#2) | `Salesforce/blip-image-captioning-base` | Descripción automática de imágenes |
| NLP — Traducción (#3) | `Helsinki-NLP/opus-mt-en-es` | Traducción inglés → español |
| NLP — Generación (#3) | `gpt2` | Generación de comentario narrativo |
| TTS — Voz (#4) | `facebook/mms-tts-spa` | Síntesis de voz en español |

---

## Módulos del Sistema

### Módulo #1 — Captura de Video (`capture_frames`)
Utiliza **OpenCV** para abrir el archivo `.mp4` y extraer frames a intervalos regulares (por defecto cada 5 segundos). Cada frame se convierte a formato RGB y se almacena junto con su timestamp.

### Módulo #2 — Visión (`describe_frame`)
Alimenta cada frame al modelo **BLIP** de Salesforce, que genera una descripción textual en inglés de lo que ocurre en la imagen (ej. `"a cartoon character cooking in a kitchen"`).

### Módulo #3 — Narración NLP (`generate_narrative_hf`)
Combina dos modelos:
1. **Helsinki-NLP/opus-mt-en-es**: traduce la descripción visual al español.
2. **GPT-2**: toma la traducción junto con el contexto temporal de la escena y genera un comentario creativo de narrador.

> **Base de conocimiento configurable:** El diccionario `CONTEXTO_POR_TIEMPO` permite definir el contexto narrativo esperado en cada segundo del video, adaptándose a cualquier contenido.

### Módulo #4 — Voz / TTS (`text_to_speech`)
El modelo **facebook/mms-tts-spa** convierte el comentario generado a un archivo `.wav` con voz sintética en español. El audio se normaliza y se exporta con `scipy.io.wavfile`.

### Módulo #5 — Lógica de Control (`should_comment`)
Implementa una **heurística de diferencia de píxeles**: compara el frame actual con el anterior (redimensionados a 64×64 para eficiencia). Si la diferencia promedio es menor a un umbral (`DIFF_THRESHOLD = 25.0`), el frame se omite y no se genera narración, evitando comentarios repetitivos en escenas estáticas.

---

## Interfaz (Gradio)

La aplicación expone una interfaz web con:
- **Entrada:** carga de archivo de video `.mp4`
- **Salida:** video con narración incrustada + log de proceso
- **Barra de progreso** en tiempo real por módulo

---

## Instalación y Uso

### Requisitos
```bash
pip install torch transformers gradio opencv-python moviepy scipy Pillow
```

### Ejecución
```bash
python main.py
```
Abre el navegador en `http://localhost:7860` y carga tu video.

### Configuración
| Parámetro | Valor por defecto | Descripción |
|-----------|-------------------|-------------|
| `FRAME_INTERVAL_SEC` | `5` | Intervalo entre frames analizados |
| `DIFF_THRESHOLD` | `25.0` | Umbral mínimo de cambio para narrar |
| `DEVICE` | `cuda` / `cpu` | Detección automática de GPU |

---

## structura del Proyecto

```
proyecto-narrador-ia/
├── main.py                 # Código principal del sistema
├── README.md               # Este archivo
└── audio_output/           # Carpeta temporal de audios generados (auto-creada)
```

---

## Posibles Mejoras Futuras

- Soporte para captura de pantalla en tiempo real (streaming)
- Reemplazar GPT-2 por un modelo más potente (ej. `mistralai/Mistral-7B`)
- Añadir clasificación de emociones al comentario con un modelo de análisis de sentimiento
- Entrenamiento de CNN propia con Transfer Learning (ResNet50) para mayor precisión contextual