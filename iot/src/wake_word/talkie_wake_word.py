import os
import sys
import pvporcupine
import pyaudio
import struct

current_dir = os.path.dirname(os.path.abspath(__file__))
src_path = os.path.join(current_dir, '..', 'src')
if src_path not in sys.path:
    sys.path.append(src_path)

from src.config.api_key import WAKEWORD_KEY
from src.logger.logger import get_logger

# 로거 설정
logger = get_logger()


# Porcupine과 PyAudio 초기화 함수
def initialize_wake_word():
    try:
        porcupine = pvporcupine.create(
            access_key=WAKEWORD_KEY,
            keyword_paths=[os.path.join(os.path.dirname(__file__), "talkie_ko_windows_v3_0_0.ppn")],
            model_path=os.path.join(os.path.dirname(__file__), "porcupine_params_ko.pv")
        )

        p = pyaudio.PyAudio()
        stream = p.open(
            rate=porcupine.sample_rate,
            channels=1,
            format=pyaudio.paInt16,
            input=True,
            frames_per_buffer=porcupine.frame_length
        )

        logger.info("[웨이크 워드 대기 중] '토키야'라고 부르면 대화가 시작됩니다.")
        print("[웨이크 워드 대기 중] '토키야'라고 부르면 대화가 시작됩니다.")
        return porcupine, stream
    except Exception as e:
        logger.error("웨이크 워드 초기화 실패: %s", e)
        raise


# 웨이크 워드 감지 함수
async def detect_wake_word(porcupine, stream):
    try:
        pcm = stream.read(porcupine.frame_length, exception_on_overflow=False)
        pcm_unpacked = struct.unpack_from("h" * porcupine.frame_length, pcm)
        keyword_index = porcupine.process(pcm_unpacked)

        if keyword_index >= 0:
            logger.info("웨이크 워드 감지됨!")
            print("웨이크 워드 감지됨!")
            return True
        return False
    except Exception as e:
        logger.error("웨이크 워드 감지 실패: %s", e)
        return False