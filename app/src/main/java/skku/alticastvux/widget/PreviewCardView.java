package skku.alticastvux.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import skku.alticastvux.R;

/**
 *  https://github.com/hitherejoe/LeanbackCards
 */
public class PreviewCardView extends FrameLayout {

    private FrameLayout mMainContainer;
    private LoopingVideoView mVideoView;
    private ImageView mImageView;
    private View mOverlayView;
    private ProgressBar mProgressCard;
    private String mVideoUrl;

    public PreviewCardView(Context context) {
        super(context);
        init();
    }

    public PreviewCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PreviewCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.widget_preview_card, this);
        view.setBackgroundColor(getResources().getColor(R.color.default_header));
        mMainContainer = (FrameLayout) view.findViewById(R.id.main_container);
        mVideoView = (LoopingVideoView) view.findViewById(R.id.main_video);
        mImageView = (ImageView) view.findViewById(R.id.main_image);
        mOverlayView = view.findViewById(R.id.view_overlay);
        mProgressCard = (ProgressBar) view.findViewById(R.id.progress_card);
    }

    public void setVideoUrl(String videoUrl) {
        mVideoUrl = videoUrl;
    }

    public void setVideoViewSize(int width, int height) {
        mVideoView.setLayoutParams(new LayoutParams(width, height));
    }

    public ImageView getImageView() {
        return mImageView;
    }

    Handler handler = new Handler();

    public void setLoading() {
        mOverlayView.setVisibility(View.VISIBLE);
        mProgressCard.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.VISIBLE);
        mOverlayView.setVisibility(View.INVISIBLE);
        mProgressCard.setVisibility(View.INVISIBLE);
        mVideoView.setupMediaPlayer(mVideoUrl, new LoopingVideoView.OnVideoReadyListener() {
            @Override
            public void onVideoReady() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setVisibility(View.INVISIBLE);
                    }
                }, 300);
            }

            @Override
            public void onVideoError() {
                setNotPlayingViewState();
            }
        });
    }

    public void setFinished() {
        mVideoView.stopMediaPlayer();
        setNotPlayingViewState();
    }

    public void setPosition(int position) {
        mVideoView.seekTo(position);
    }
    private void setNotPlayingViewState() {
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);
        mOverlayView.setVisibility(View.INVISIBLE);
        mProgressCard.setVisibility(View.INVISIBLE);
    }

}