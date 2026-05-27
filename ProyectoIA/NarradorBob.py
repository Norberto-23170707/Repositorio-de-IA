import sys
import cv2
import torch
import numpy as np
import os
import shutil
import gradio as gr
from PIL import Image
from transformers import (
    BlipProcessor,
    BlipForConditionalGeneration,
    VitsModel,
    AutoTokenizer as VitsTokenizer,
    pipeline,
)
import scipy.io.wavfile as wavfile

# Importación híbrida y segura para MoviePy (Cero fallos por versión)
try:
    from moviepy.editor import VideoFileClip, AudioFileClip, CompositeAudioClip
except ModuleNotFoundError:
    from moviepy.video.io.VideoFileClip import VideoFileClip
    from moviepy.audio.io.AudioFileClip import AudioFileClip
    from moviepy.audio.AudioClip import CompositeAudioClip

import warnings
warnings.filterwarnings("ignore")

# ─────────────────────────────────────────────
# CONFIGURACIÓN GENERAL
# ─────────────────────────────────────────────
OUTPUT_AUDIO_DIR = "audio_output"
FRAME_INTERVAL_SEC = 5  
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
PIPELINE_DEVICE = 0 if DEVICE == "cuda" else -1
DIFF_THRESHOLD = 25.0

# Asegurar un directorio limpio desde el inicio
if os.path.exists(OUTPUT_AUDIO_DIR):
    shutil.rmtree(OUTPUT_AUDIO_DIR)
os.makedirs(OUTPUT_AUDIO_DIR, exist_ok=True)

# Base de conocimiento: Contexto temporal de la escena
CONTEXTO_POR_TIEMPO = {
    0:  "Bob Esponja está cocinando cangreburgers en el Crustáceo Crujiente, todo va bien.",
    5:  "A Bob se le rompe la espátula mientras cocinaba y empieza a gritar desesperado.",
    10: "Bob está histérico por la espátula rota, completamente fuera de control.",
    15: "Bob le da primeros auxilios a su espátula rota como si fuera una persona.",
    20: "Plankton observa el caos y se alegra porque todo está saliendo mal en el restaurante.",
    25: "Don Cangrejo mira a Bob con cara de horror mientras le pone toques a la espátula.",
    30: "Don Cangrejo le dice a Bob que se controle porque está actuando como loco.",
    35: "Don Cangrejo le ordena a Bob que vaya a comprar una espátula nueva.",
    40: "Bob intenta llorar para no ir a comprar la espátula pero Don Cangrejo se va molesto.",
    45: "Bob va camino a la tienda de espátulas más grande del océano.",
    50: "Bob llega a la tienda de espátulas y se sorprende al ver el tamaño enorme del lugar.",
    55: "Bob está asombrado dentro de la tienda rodeo de miles de espátulas.",
}

# ─────────────────────────────────────────────
# MÓDULO #1 — CAPTURA DE VIDEO (OpenCV)
# ─────────────────────────────────────────────
def capture_frames(video_path: str, interval_sec: int = 5):
    clean_path = os.path.normpath(os.path.abspath(video_path))
    cap = cv2.VideoCapture(clean_path)
    if not cap.isOpened():
        raise FileNotFoundError(f"No se pudo abrir el video: {clean_path}")

    fps = cap.get(cv2.CAP_PROP_FPS)
    if fps == 0:
        fps = 24
    frame_step = int(fps * interval_sec)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

    frames = []
    frame_idx = 0

    while frame_idx < total_frames:
        cap.set(cv2.CAP_PROP_POS_FRAMES, frame_idx)
        ret, frame = cap.read()
        if not ret:
            break

        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        pil_image = Image.fromarray(frame_rgb)
        timestamp = frame_idx / fps
        frames.append((timestamp, pil_image, frame_rgb))
        
        frame_idx += frame_step

    cap.release()
    return frames

# ─────────────────────────────────────────────
# MÓDULO #5 — LÓGICA DE CONTROL / HEURÍSTICA
# ─────────────────────────────────────────────
def should_comment(current_frame_rgb: np.ndarray, prev_frame_rgb: np.ndarray or None) -> bool:
    if prev_frame_rgb is None:
        return True

    h, w = 64, 64
    curr_small = cv2.resize(current_frame_rgb, (w, h)).astype(np.float32)
    prev_small = cv2.resize(prev_frame_rgb, (w, h)).astype(np.float32)

    diff = np.mean(np.abs(curr_small - prev_small))
    print(f"  [HEURÍSTICA] Variación de la escena: {diff:.2f}", flush=True)
    return diff >= DIFF_THRESHOLD

# ─────────────────────────────────────────────
# MÓDULO #2 — VISIÓN (BLIP de Hugging Face)
# ─────────────────────────────────────────────
def load_vision_model():
    print("[#2 VISIÓN] Cargando Salesforce/blip-image-captioning-base...", flush=True)
    processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
    model = BlipForConditionalGeneration.from_pretrained(
        "Salesforce/blip-image-captioning-base"
    ).to(DEVICE)
    model.eval()
    return processor, model

def describe_frame(image: Image.Image, processor, model) -> str:
    inputs = processor(images=image, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        output_ids = model.generate(
            **inputs, 
            max_new_tokens=40, 
            repetition_penalty=1.5,
            no_repeat_ngram_size=2
        )
    return processor.decode(output_ids[0], skip_special_tokens=True)

# ─────────────────────────────────────────────
# MÓDULO #3 — NLP / NARRACIÓN
# ─────────────────────────────────────────────
def load_nlp_models():
    translator = None
    try:
        translator = pipeline(
            "translation_en_to_es",
            model="Helsinki-NLP/opus-mt-en-es",
            device=PIPELINE_DEVICE
        )
    except Exception:
        try:
            translator = pipeline(
                "translation",
                model="Helsinki-NLP/opus-mt-en-es",
                device=PIPELINE_DEVICE
            )
        except Exception:
            translator = None
    
    print("[#3 NLP] Cargando generador gpt2...", flush=True)
    generator = pipeline(
        "text-generation",
        model="gpt2",
        device=PIPELINE_DEVICE
    )
    return translator, generator

def get_contexto(timestamp: float) -> str:
    claves = sorted(CONTEXTO_POR_TIEMPO.keys())
    if not claves:
        return "Todo normal en Fondo de Bikini."
    clave = min(claves, key=lambda x: abs(x - timestamp))
    return CONTEXTO_POR_TIEMPO[clave]

def generate_narrative_hf(description: str, timestamp: float, translator, generator) -> str:
    contexto = get_contexto(timestamp)
    
    if translator is not None:
        try:
            traduccion = translator(description, max_length=40)[0]["translation_text"]
        except Exception:
            traduccion = description
    else:
        traduccion = description

    comentario_base = f"{contexto} Se distingue {traduccion}."
    
    try:
        res = generator(comentario_base, max_new_tokens=12, do_sample=True, temperature=0.7)[0]["generated_text"]
        comentario = res.split("\n")[0][:90].strip()
        if not comentario.endswith("."):
            comentario += "."
    except Exception:
        comentario = comentario_base[:90].strip()
        if not comentario.endswith("."):
            comentario += "."

    print(f"  [HF NLP] Comentario final: {comentario}", flush=True)
    return comentario

# ─────────────────────────────────────────────
# MÓDULO #4 — AUDIO / TTS (MMS-TTS de Hugging Face)
# ─────────────────────────────────────────────
def load_tts_model():
    print("[#4 VOZ] Cargando facebook/mms-tts-spa...", flush=True)
    tokenizer = VitsTokenizer.from_pretrained("facebook/mms-tts-spa")
    model = VitsModel.from_pretrained("facebook/mms-tts-spa").to(DEVICE)
    model.eval()
    return tokenizer, model

def text_to_speech(text: str, tokenizer, model, output_path: str):
    inputs = tokenizer(text, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        output = model(**inputs)

    waveform = output.waveform[0].cpu().numpy()
    max_val = np.max(np.abs(waveform))
    if max_val > 0:
        waveform = waveform / max_val
    waveform_int16 = np.int16(waveform * 32767)
    sample_rate = model.config.sampling_rate

    wavfile.write(output_path, sample_rate, waveform_int16)
    return output_path

# ────────────────────────────────────────────────────
# ENSAMBLADOR DE VIDEO (MUTEO ABSOLUTO DEL ORIGINAL)
# ────────────────────────────────────────────────────
def merge_audio_to_video(video_path: str, results: list):
    print("[MIXER] Mezclando clips de audio en la línea de tiempo...", flush=True)
    clean_video_path = os.path.normpath(os.path.abspath(video_path))
    
    video_original = VideoFileClip(clean_video_path)
    
    if hasattr(video_original, "without_audio"):
        video = video_original.without_audio()
    else:
        video = video_original.set_audio(None)
        
    audio_clips = []
    next_available_start = 0.0  # ← FIX: controla cuándo puede iniciar el siguiente clip

    for r in results:
        # Respeta el timestamp original pero nunca antes de que el clip anterior termine
        start_time = max(r["timestamp_sec"], next_available_start)

        audio_clip = AudioFileClip(r["audio_file"])
        clip_duration = audio_clip.duration

        if hasattr(audio_clip, "with_start"):
            audio_clip = audio_clip.with_start(start_time)
        else:
            audio_clip = audio_clip.set_start(start_time)

        audio_clips.append(audio_clip)
        next_available_start = start_time + clip_duration + 0.3  # 0.3s de pausa entre narraciones

    if not audio_clips:
        output_path = "video_narrado.mp4"
        video.write_videofile(output_path, codec="libx264", audio=False, logger=None)
        video.close()
        video_original.close()
        return output_path

    audio_compuesto = CompositeAudioClip(audio_clips)
    
    if hasattr(video, "with_audio"):
        video_final = video.with_audio(audio_compuesto)
    else:
        video_final = video.set_audio(audio_compuesto)
        
    output_path = "video_narrado.mp4"
    
    if os.path.exists(output_path):
        try:
            os.remove(output_path)
        except Exception:
            pass

    video_final.write_videofile(output_path, codec="libx264", audio_codec="aac", logger=None)
    
    for clip in audio_clips:
        clip.close()
    audio_compuesto.close()
    video.close()
    video_original.close()
    video_final.close()
    
    return output_path

# ─────────────────────────────────────────────
# PIPELINE INTEGRADO
# ─────────────────────────────────────────────
def run_pipeline(video_path: str, progress=gr.Progress()):
    if not video_path:
        return None, "Error: Carga un archivo de video válido primero."

    if os.path.exists(OUTPUT_AUDIO_DIR):
        shutil.rmtree(OUTPUT_AUDIO_DIR)
    os.makedirs(OUTPUT_AUDIO_DIR, exist_ok=True)

    print(f"[RUTA] Procesando archivo de video en: {video_path}", flush=True)

    progress(0, desc="Iniciando Modelos Locales...")
    blip_processor, blip_model = load_vision_model()
    translator, generator = load_nlp_models()
    tts_tokenizer, tts_model = load_tts_model()

    progress(0.2, desc="Analizando cuadros de animación...")
    frames = capture_frames(video_path, interval_sec=FRAME_INTERVAL_SEC)

    results = []
    skipped = 0
    prev_frame_rgb = None
    log_lines = []

    for i, (timestamp, image, frame_rgb) in enumerate(frames):
        progress((0.2 + 0.6 * ((i + 1) / len(frames))), desc=f"Procesando fragmento {i+1}/{len(frames)}...")
        
        if not should_comment(frame_rgb, prev_frame_rgb):
            skipped += 1
            log_lines.append(f"[t={timestamp:.1f}s] Cuadro Omitido (Escena estática).")
            continue

        prev_frame_rgb = frame_rgb

        description = describe_frame(image, blip_processor, blip_model)
        comment = generate_narrative_hf(description, timestamp, translator, generator)

        audio_filename = os.path.join(OUTPUT_AUDIO_DIR, f"audio_run_{i+1:03d}.wav")
        text_to_speech(comment, tts_tokenizer, tts_model, audio_filename)

        results.append({
            "frame": i + 1,
            "timestamp_sec": timestamp,
            "audio_file": audio_filename,
        })

        log_lines.append(
            f"[t={timestamp:.1f}s] Narración Generada\n"
            f"  Visión: {description}\n"
            f"  NLP: {comment}"
        )

    if not results:
        return video_path, "El video no cambió lo suficiente para narrarlo."

    progress(0.9, desc="Renderizando archivo final...")
    output_video = merge_audio_to_video(video_path, results)
    
    log_lines.append(f"\n[FIN] Cuadros con voz: {len(results)} | Cuadros filtrados: {skipped}")
    return output_video, "\n\n".join(log_lines)

# ─────────────────────────────────────────────
# INTERFAZ DE GRADIO
# ─────────────────────────────────────────────
def build_interface():
    with gr.Blocks(title="Narrador BobEsponja IA", theme=gr.themes.Soft()) as demo:
        gr.Markdown("""
        #Narrador de Video con IA Multimodal
        **Orquestación de Modelos de Inteligencia Artificial locales usando Hugging Face**
        """)

        with gr.Row():
            with gr.Column():
                video_input = gr.Video(label="Sube aquí tu video a narrar (.mp4)")
                run_btn = gr.Button("▶ Ejecutar Sistema Multimodal", variant="primary", size="lg")

            with gr.Column():
                video_output = gr.Video(label="Video Final con Narración")
                log_output = gr.Textbox(label="Log del Módulo de Control", lines=15, interactive=False)

        run_btn.click(
            fn=run_pipeline,
            inputs=[video_input],
            outputs=[video_output, log_output],
        )

    return demo

if __name__ == "__main__":
    demo = build_interface()
    demo.launch(share=False)