package skku.alticastvux.util;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.io.File;

public class AudioFromVideo {
    private String audio, video;
    private MediaCodec amc;
    private MediaExtractor ame;
    private MediaFormat amf;
    private String amime;
    private OnFinishListener listener;
    private int start;
    private int duration;

    public interface OnFinishListener {
        void finish();
    }

    public AudioFromVideo(String srcVideo, String destAudio, int start, int duration, OnFinishListener listener) {
        this.audio = destAudio;
        this.video = srcVideo;
        this.listener = listener;
        this.start = start; //seconds
        this.duration = duration; //seconds
        ame = new MediaExtractor();
        init();
        start();
    }

    public AudioFromVideo init() {
        try {
            ame.setDataSource(video);
            amf = ame.getTrackFormat(1);
            ame.selectTrack(1);
            amime = amf.getString(MediaFormat.KEY_MIME);
            amc = MediaCodec.createDecoderByType(amime);
            amc.configure(amf, null, null, 0);
            amc.start();



        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void start() {
        new AudioService(amc, ame, audio).start();
    }

    private class AudioService extends Thread {
        private MediaCodec amc;
        private MediaExtractor ame;
        private ByteBuffer[] aInputBuffers, aOutputBuffers;
        private String destFile;

        AudioService(MediaCodec amc, MediaExtractor ame, String destFile) {
            this.amc = amc;
            this.ame = ame;
            this.destFile = destFile;
            aInputBuffers = amc.getInputBuffers();
            aOutputBuffers = amc.getOutputBuffers();
        }

        public void run() {
            int sampleRate = amf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int channels = amf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            try {
                OutputStream os = new FileOutputStream(new File(destFile+".tmp"));
                long count = 0;
                ame.seekTo(1000000 * start, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                while (true) {
                    if(count >= sampleRate * channels * 2 * duration) {
                        break;
                    }
                    int inputIndex = amc.dequeueInputBuffer(0);
                    if (inputIndex == -1) {
                        continue;
                    }
                    int sampleSize = ame.readSampleData(aInputBuffers[inputIndex], 0);
                    if (sampleSize == -1) break;
                    long presentationTime = ame.getSampleTime();
                    int flag = ame.getSampleFlags();
                    ame.advance();
                    amc.queueInputBuffer(inputIndex, 0, sampleSize, presentationTime, flag);
                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    int outputIndex = amc.dequeueOutputBuffer(info, 0);
                    if (outputIndex >= 0) {
                        byte[] data = new byte[info.size];
                        aOutputBuffers[outputIndex].get(data, 0, data.length);
                        aOutputBuffers[outputIndex].clear();
                        os.write(data);
                        count += data.length;
                        amc.releaseOutputBuffer(outputIndex, false);
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        aOutputBuffers = amc.getOutputBuffers();
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    }
                }
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                AudioUtil.rawToWave(new File(destFile+".tmp"), new File(destFile), sampleRate, channels);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(listener != null) {
                listener.finish();
            }
        }
    }
}