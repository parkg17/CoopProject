package skku.alticastvux.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.VideoView;

import skku.alticastvux.R;

/**
 * Created by woorim on 2018. 7. 26..
 */

public class VideoBackgroundManager {

    private Context mContext;
    private Window mWindow;
    private VideoView videoView;

    public VideoBackgroundManager(Window window) {
        mContext = window.getContext();
        mWindow = window;
        initView();
    }

    private void initView() {
        View layout = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.videoview_test, null);
        videoView = layout.findViewById(R.id.background_videoview);
        FrameLayout rootView = (FrameLayout) mWindow.getDecorView();
        rootView.addView(layout, 0);
    }

    public void setVideoPath(String path) {
        videoView.setVideoPath(path);
    }

    public void startVideo() {
        videoView.start();
    }

    public void seekTo(int ms) {
        videoView.seekTo(ms);
    }

}
