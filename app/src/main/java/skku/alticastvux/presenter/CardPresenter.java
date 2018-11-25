/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package skku.alticastvux.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import skku.alticastvux.R;
import skku.alticastvux.model.VideoInfo;
import skku.alticastvux.util.Util;
import skku.alticastvux.widget.LiveCardView;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;

    private static void updateCardBackgroundColor(View view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);
        /*
         * This template uses a default image in res/drawable, but the general case for Android TV
         * will require your resources in xhdpi. For more information, see
         * https://developer.android.com/training/tv/start/layouts.html#density-resources
         */


        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.movie);

        final Handler handler = new Handler();
        final LiveCardView liveCardView = new LiveCardView(parent.getContext()) {
            boolean pselect = false;
            @Override
            public void setSelected(boolean selected) {
                pselect = selected;
                if (selected) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(pselect) {
                                startVideo();
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(videoInfo.getPath());
                                long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                                setPosition((int)duration/2);
                            }
                        }
                    }, 600);
                } else {
                    stopVideo();
                }
                updateCardBackgroundColor(this, selected);
                super.setSelected(true);
            }
        };


        liveCardView.setFocusable(true);
        liveCardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(liveCardView, false);
        return new ViewHolder(liveCardView);
    }

    VideoInfo videoInfo;

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        videoInfo = (VideoInfo) ((CardInfo)item).getObject("videoInfo");
        LiveCardView cardView = (LiveCardView) viewHolder.view;

        Log.d(TAG, "onBindViewHolder");
        if (videoInfo.getName() != null) {
            cardView.setTitleText(videoInfo.getTitle());
            cardView.setContentText(videoInfo.getPath());
            cardView.setVideoViewSize(CARD_WIDTH, CARD_HEIGHT);
            cardView.setVideoUrl(videoInfo.getPath());

            Glide.with(viewHolder.view.getContext())
                    .load(Util.getThumbnailByteArray(videoInfo.getPath()))
                    .asBitmap()
                    .centerCrop()
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
        LiveCardView cardView = (LiveCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        //cardView.setBadgeImage(null);
        //cardView.setMainImage(null);
    }
}
