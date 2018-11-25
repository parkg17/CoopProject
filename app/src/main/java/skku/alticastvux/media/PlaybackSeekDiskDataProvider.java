/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package skku.alticastvux.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.util.Log;

import org.apache.commons.collections4.map.HashedMap;

import java.io.File;

import skku.alticastvux.app.SKKUVuxApp;

import java.util.HashMap;

import skku.alticastvux.util.Util;

/**
 * Sample PlaybackSeekDataProvider that reads bitmaps stored on disk.
 * e.g. new PlaybackSeekDiskDataProvider(duration, 1000, "/sdcard/frame_%04d.jpg")
 * Expects the seek positions are 1000ms interval, snapshots are stored at
 * /sdcard/frame_0001.jpg, ...
 */
public class PlaybackSeekDiskDataProvider extends PlaybackSeekAsyncDataProvider {

    final Paint mPaint;
    final String mPathPattern;

    PlaybackSeekDiskDataProvider(long duration, long interval, String pathPattern) {
        mPathPattern = pathPattern;
        int size = (int) (duration / interval) + 1;
        long[] pos = new long[size];
        for (int i = 0; i < pos.length; i++) {
            pos[i] = i * duration / pos.length;
        }
        setSeekPositions(pos);
        mPaint = new Paint();
        mPaint.setTextSize(16);
        mPaint.setColor(Color.BLUE);
        map = new HashMap<>();
    }

    HashMap<Integer, Bitmap> map;

    protected Bitmap doInBackground(Object task, final int index, final long position) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // Thread might be interrupted by cancel() call.
        }
        if (isCancelled(task)) {
            return null;
        }

        if (map.get(index) == null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mPathPattern);
            Bitmap thumb = retriever.getFrameAtTime(position * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            Bitmap s = Bitmap.createScaledBitmap(thumb, thumb.getWidth() / 5, thumb.getHeight() / 5, true);
            thumb = null;
            map.put(index, s);
        }
        return map.get(index);
    }

    /**
     * Helper function to set a demo seek provider on PlaybackTransportControlGlue based on
     * duration.
     */
    public static void setDemoSeekProvider(final PlaybackTransportControlGlue glue, final String videoPath) {
        if (glue.isPrepared()) {
            glue.setSeekProvider(new PlaybackSeekDiskDataProvider(
                    glue.getDuration(),
                    glue.getDuration() / 100,
                    videoPath));
        } else {
            glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                @Override
                public void onPreparedStateChanged(PlaybackGlue glue) {
                    if (glue.isPrepared()) {
                        glue.removePlayerCallback(this);
                        PlaybackTransportControlGlue transportControlGlue =
                                (PlaybackTransportControlGlue) glue;
                        transportControlGlue.setSeekProvider(new PlaybackSeekDiskDataProvider(
                                transportControlGlue.getDuration(),
                                transportControlGlue.getDuration() / 100,
                                videoPath));
                    }
                }
            });
        }
    }

}
