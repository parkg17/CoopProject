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

package skku.alticastvux.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnAssetFetch;
import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import skku.alticastvux.R;
import skku.alticastvux.activity.base.BaseFragmentActivity;
import skku.alticastvux.adapter.GridMenuAdapter;
import skku.alticastvux.animation.ResizeWidthAnimation;
import skku.alticastvux.baedalfragment.MenuSearchFragment;
import skku.alticastvux.data.StoreMenu;
import skku.alticastvux.ffmpeg.FFmpegWrapper;
import skku.alticastvux.fragment.PlaybackVideoFragment;
import skku.alticastvux.gracenote.GracenoteClient;
import skku.alticastvux.model.Menu;
import skku.alticastvux.util.AudioFromVideo;
import skku.alticastvux.util.DBUtil;
import skku.alticastvux.util.FragmentStackV4;
import skku.alticastvux.util.Util;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.AddBookMarkPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;

/**
 * Loads {@link PlaybackVideoFragment}.
 */
public class PlaybackActivity extends BaseFragmentActivity implements CommandListener {

    @BindView(R.id.tv_song_album)
    TextView tv_album;

    @BindView(R.id.tv_song_artist)
    TextView tv_artist;

    @BindView(R.id.tv_song_name)
    TextView tv_song;

    @BindView(R.id.layout_songinfo)
    LinearLayout layout_songinfo;

    @BindView(R.id.circularImageView)
    CircularImageView iv_coverart;

    @BindView(R.id.layout_songinforoot)
    RelativeLayout layout_songinforoot;

    @BindView(R.id.fragment_container)
    LinearLayout fragment_container;

    @BindView(R.id.layout_order)
    LinearLayout layout_order;

    @BindView(R.id.recycler_checkout)
    RecyclerView recycler_checkout;

    int height = 0;
    int width = 0;

    boolean minimized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_playback);
            ButterKnife.bind(this);
        }

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fragment_container.getLayoutParams();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                minimizeFragment();
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //maximizeFragment();
            }
        }, 1500);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_checkout.setLayoutManager(layoutManager);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                width = getWindow().getDecorView().getWidth();
                height = getWindow().getDecorView().getHeight();
            }
        });
    }

    ArrayList<StoreMenu> checkoutList = new ArrayList<>();

    public void addItemToCheckoutList(StoreMenu s) {
        checkoutList.add(s);
        recycler_checkout.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_card_2, parent, false);
                RecyclerView.ViewHolder holder = new ViewHolder(v);
                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
                ViewHolder holder = (ViewHolder) h;
                StoreMenu s = checkoutList.get(position);
                Picasso.get().load(s.thumUrl).into(((ViewHolder) h).imageView);
                ((ViewHolder) h).textView.setText(s.name);
            }

            @Override
            public int getItemCount() {
                return checkoutList.size();
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.grid_item_iv);
            textView = (TextView) view.findViewById(R.id.grid_item_text);
        }
    }


    private void maximizeFragment() {
        minimized = false;
        ObjectAnimator anim = ObjectAnimator.ofFloat(fragment_container, "scaleX", 1.0f);
        anim.setDuration(1000);
        anim.start();
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(fragment_container, "scaleY", 1.0f);
        anim2.setDuration(1000);
        anim2.start();
        AnimatorSet animSetXY = new AnimatorSet();
        ObjectAnimator y = ObjectAnimator.ofFloat(fragment_container,
                "translationY", -(height * 17.5f / 100.0f) + 50, 0);
        ObjectAnimator x = ObjectAnimator.ofFloat(fragment_container,
                "translationX", -(width * 17.5f / 100.0f) + 50, 0);
        animSetXY.playTogether(x, y);
        animSetXY.setDuration(1000);
        animSetXY.start();

        ObjectAnimator x2 = ObjectAnimator.ofFloat(layout_order,
                "translationX", 0, Util.dpToPixels(this, 300));
        x2.setDuration(1000);
        x2.start();

        // init fragment
        FragmentStackV4.clear(getSupportFragmentManager());
    }

    public void minimizeFragment() {
        minimized = true;
        ObjectAnimator anim = ObjectAnimator.ofFloat(fragment_container, "scaleX", 0.65f);
        anim.setDuration(1000);
        anim.start();
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(fragment_container, "scaleY", 0.65f);
        anim2.setDuration(1000);
        anim2.start();
        AnimatorSet animSetXY = new AnimatorSet();
        ObjectAnimator y = ObjectAnimator.ofFloat(fragment_container,
                "translationY", 0, -(height * 17.5f / 100.0f) + 50);
        ObjectAnimator x = ObjectAnimator.ofFloat(fragment_container,
                "translationX", 0, -(width * 17.5f / 100.0f) + 50);
        animSetXY.playTogether(x, y);
        animSetXY.setDuration(1000);
        animSetXY.start();
        fragment_container.setFocusable(false);
        fragment_container.setFocusableInTouchMode(false);

        layout_order.setFocusable(true);
        layout_order.setFocusableInTouchMode(true);
        layout_order.requestFocus();
        layout_order.setVisibility(View.VISIBLE);
        ObjectAnimator x2 = ObjectAnimator.ofFloat(layout_order,
                "translationX", Util.dpToPixels(this, 300), 0);
        x2.setDuration(1000);
        x2.start();
        FragmentStackV4.init(getSupportFragmentManager(), R.id.layout_order, new MenuSearchFragment());
    }

    @Override
    public void onBackPressed() {
        if (minimized) {
            if (FragmentStackV4.count == 0) {
                maximizeFragment();
            }
            if (FragmentStackV4.count > 0) {
                FragmentStackV4.back(getSupportFragmentManager());
            }
        } else {
            super.onBackPressed();
        }
    }

    GracenoteClient client;

    public void findSong(String filename, long position) {
        Log.e(getClass().getSimpleName(), "find song : " + filename + " " + position / 1000);
        pAlbum = null;
        final int seconds = (int) (position / 1000L);

        client = new GracenoteClient(this);
        final IGnMusicIdStreamEvents listener = new IGnMusicIdStreamEvents() {
            @Override
            public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus gnMusicIdStreamProcessingStatus, IGnCancellable iGnCancellable) {
                Log.e("GracenoteClient", "musicIdStreamProcessingStatusEvent");
            }

            @Override
            public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus gnMusicIdStreamIdentifyingStatus, IGnCancellable iGnCancellable) {
                Log.e("GracenoteClient", "musicIdStreamIdentifyingStatusEvent " + gnMusicIdStreamIdentifyingStatus.toString());
            }

            @Override
            public void musicIdStreamAlbumResult(final GnResponseAlbums gnResponseAlbums, IGnCancellable iGnCancellable) {
                Log.e("GracenoteClient", "AlbumResult");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gnResponseAlbums.resultCount() == 0) {
                            Toast.makeText(PlaybackActivity.this, "no match", 0).show();
                        } else {

                        }

                        GnAlbumIterator iter = gnResponseAlbums.albums().getIterator();
                        while (iter.hasNext()) {
                            try {
                                final GnAlbum album = iter.next();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showShowInfo(album);
                                    }
                                });
                                break;
                            } catch (GnException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void musicIdStreamIdentifyCompletedWithError(GnError gnError) {
                Log.e("GracenoteClient", "musicIdStreamIdentifyCompletedWithError");
            }

            @Override
            public void statusEvent(GnStatus gnStatus, long l, long l1, long l2, IGnCancellable iGnCancellable) {

            }
        };

        try {
            String command[] = Util.makeFFmpegExtractMusicCommand(filename, "/sdcard/output.wav", seconds - 5, 30);
            FFmpegWrapper wrapper = new FFmpegWrapper(PlaybackActivity.this, new FFmpegWrapper.OnCommandFinishListener() {
                @Override
                public void finish(String command) {
                    client.findSong("/sdcard/output.wav", listener);
                }
            });
            wrapper.execFFmpegBinary(command);

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e1) {

        }


    }

    GnAlbum pAlbum = null;

    Handler handler = new Handler();

    private void showShowInfo(GnAlbum album) {
        if (album == null || (pAlbum != null && album.trackMatched().title().display().equals(pAlbum.trackMatched().title().display())))
            return;
        pAlbum = album;
        String title = album.trackMatched().title().display();
        String albumname = album.title().display();
        String artist = album.trackMatched().artist().name().display();
        if (artist.isEmpty()) {
            artist = album.artist().name().display();
        }

        tv_album.setText(albumname);
        tv_artist.setText(artist);
        tv_song.setText(title);

        String coverArtUrl = album.coverArt().asset(GnImageSize.kImageSizeSmall).url();
        loadCoverArt(coverArtUrl);
        //animation
        layout_songinfo.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        layout_songinfo.startAnimation(animation);

        layout_songinforoot.getLayoutParams().width = (int) Util.dpToPixels(this, 120);
        layout_songinforoot.requestLayout();

        ResizeWidthAnimation resizeWidthAnimation = new ResizeWidthAnimation(layout_songinforoot, (int) Util.dpToPixels(this, 120), (int) Util.dpToPixels(this, 400));
        resizeWidthAnimation.setDuration(1000);
        resizeWidthAnimation.setStartOffset(500);
        resizeWidthAnimation.setInterpolator(new FastOutSlowInInterpolator());
        layout_songinforoot.startAnimation(resizeWidthAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
                animation1.setDuration(500);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        layout_songinfo.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                layout_songinfo.startAnimation(animation1);
            }
        }, 10000);
    }

    private void loadCoverArt(final String coverArtUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Drawable coverArt = null;
                GracenoteClient client = new GracenoteClient(PlaybackActivity.this);
                if (coverArtUrl != null && !coverArtUrl.isEmpty()) {
                    try {
                        GnAssetFetch assetData = new GnAssetFetch(client.getGnUser(), coverArtUrl);
                        byte[] data = assetData.data();
                        coverArt = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
                    } catch (GnException e) {
                        e.printStackTrace();
                    }
                    setCoverArt(coverArt);
                }
            }
        }).start();
    }

    private void setCoverArt(final Drawable coverArt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (coverArt != null) {
                    iv_coverart.setImageDrawable(coverArt);
                } else {

                }
            }
        });
    }
}