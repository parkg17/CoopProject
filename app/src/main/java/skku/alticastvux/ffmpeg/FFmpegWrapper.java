package skku.alticastvux.ffmpeg;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class FFmpegWrapper {

    FFmpeg ffmpeg;
    Context context;
    OnCommandFinishListener listener;

    public interface OnCommandFinishListener {
        public abstract void finish(String command);
    }

    public FFmpegWrapper(Context context, OnCommandFinishListener listener) {
        this.listener = listener;
        this.context = context;
        ffmpeg = FFmpeg.getInstance(context);
        loadFFMpegBinary();
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {

        }
    }

    public void execFFmpegBinary(final String command[]) {
        String comm = "";
        for (int i = 0; i < command.length; i++)
            comm += command[i] + " ";
        final String k = comm;
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.e("FFmpegWrapper", "onFailure : ");
                }

                @Override
                public void onSuccess(String s) {
                    Log.e("FFmpegWrapper", "onSuccess");
                }

                @Override
                public void onProgress(String s) {

                }

                @Override
                public void onStart() {
                    Log.e("FFmpegWrapper", "onStart");
                }

                @Override
                public void onFinish() {
                    Log.e("FFmpegWrapper", "onFinish");
                    if (listener != null) {
                        listener.finish(k);
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

}
