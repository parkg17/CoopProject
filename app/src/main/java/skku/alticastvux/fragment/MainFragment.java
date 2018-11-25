/*
 * Copyright (C) 2017 The Android Open Source Project
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

package skku.alticastvux.fragment;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import skku.alticastvux.activity.BookmarkActivity;
import skku.alticastvux.activity.DetailsActivity;
import skku.alticastvux.activity.MainActivity;
import skku.alticastvux.app.SKKUVuxApp;
import skku.alticastvux.R;
import skku.alticastvux.model.Genre;
import skku.alticastvux.model.VideoInfo;
import skku.alticastvux.presenter.CardInfo;
import skku.alticastvux.presenter.CardPresenterSelector;
import skku.alticastvux.util.DBUtil;
import skku.alticastvux.util.Util;
import skku.alticastvux.util.VideoBackgroundManager;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.AddGenrePattern;
import skku.alticastvux.voiceable.pattern.ChangeGenrePattern;
import skku.alticastvux.voiceable.pattern.RefreshPattern;
import skku.alticastvux.voiceable.pattern.ShowDetailPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;
import skku.alticastvux.widget.CustomTitleView;
import skku.alticastvux.widget.LiveCardView;

public class MainFragment extends BrowseFragment implements CommandListener {
    private static final String TAG = "MainFragment";

    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private ArrayObjectAdapter mRowsAdapter;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;
    private Object currentItem;
    List<VideoInfo> videoInfos;

    VideoBackgroundManager videoBackgroundManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setListener(this);

        videoInfos = Util.getAllVideos();

        setupUIElements();

        loadRows();

        setupEventListeners();

        setTitle("비디오");

        videoBackgroundManager = new VideoBackgroundManager(getActivity().getWindow());
        videoBackgroundManager.setVideoPath(videoInfos.get((int) (Math.random() * videoInfos.size())).getPath());
    }


    @Override
    public void onResume() {
        super.onResume();
        videoBackgroundManager.startVideo();
    }


    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenterSelector cardPresenterSelector = new CardPresenterSelector(getActivity());

        ArrayList<Genre> genreList = DBUtil.getInstance().getGenres();


        int i = 0;
        int j = 0;
        for (Genre genre : genreList) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenterSelector);
            ArrayList<VideoInfo> vList = DBUtil.getInstance().getVideos(genre.getId());

            for (VideoInfo videoInfo : vList) {
                CardInfo cardInfo = new CardInfo(i, j++, 0);
                cardInfo.putObject("videoInfo", videoInfo);
                listRowAdapter.add(cardInfo);
            }
            HeaderItem header = new HeaderItem(i, genre.getName());
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
            j = 0;
            i++;
        }

        /*
        HeaderItem gridHeader = new HeaderItem(i, "PREFERENCES");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        */

        setAdapter(mRowsAdapter);
    }

    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        if (pattern instanceof AddGenrePattern) {
            DBUtil.getInstance().addGenre(((AddGenrePattern) pattern).getGenreName());
            refresh();
        } else if (pattern instanceof RefreshPattern) {
            refresh();
        } else if (pattern instanceof ChangeGenrePattern) {
            ChangeGenrePattern c = (ChangeGenrePattern) pattern;
            for (Genre g : DBUtil.getInstance().getGenres()) {
                if (g.getName().equals(c.getGenre())) {
                    Log.e("match", currentItem.getClass().getSimpleName());
                    CardInfo card = (CardInfo) currentItem;
                    VideoInfo v = (VideoInfo) card.getObject("videoInfo");
                    DBUtil.getInstance().removeVideoInfo(v.getGenre(), v);
                    DBUtil.getInstance().addVideoInfo(g.getId(), v);
                    refresh();
                    break;
                }
            }
        } else if (pattern instanceof ShowDetailPattern) {
            if (currentItem instanceof CardInfo) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.VIDEO_INFO, (VideoInfo) ((CardInfo) currentItem).getObject("videoInfo"));
                getActivity().startActivity(intent);
            }
        }
        return false;
    }

    public class CustomListRowPresenter extends RowPresenter {
        public CustomListRowPresenter() {
            super();
            setHeaderPresenter(new CustomRowHeaderPresenter());
        }

        @Override
        protected ViewHolder createRowViewHolder(ViewGroup parent) {
            return null;
        }
    }

    class CustomRowHeaderPresenter extends RowHeaderPresenter {

        @Override
        public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
            HeaderItem headerItem = item == null ? null : ((Row) item).getHeaderItem();
            RowHeaderPresenter.ViewHolder vh = (RowHeaderPresenter.ViewHolder) viewHolder;
            TextView title = vh.view.findViewById(R.id.row_header);
            if (!TextUtils.isEmpty(headerItem.getName())) {
                title.setText(headerItem.getName());
                title.setTextColor(ContextCompat.getColor(SKKUVuxApp.getInstance(),
                        android.R.color.holo_blue_dark));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        }
    }


    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookmarkActivity.class));
                /*
                For Test Zone
                 */
//                if(BookMarkUtil.AddVideoToBookMark("미분류",Util.getAllVideos().get(3))){
//                    refresh();
//                }
//                BookMarkUtil.AddBookMark("예능");
//                BookMarkUtil.AddBookMark("먹방");

                //BookMarkUtil.DeleteBookMark("예능");
//                if(BookMarkUtil.AddBookMark("감사")){
//                    refresh();
//                }
//                if(BookMarkUtil.DeleteBookMark("미분류")){
//
//                }
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(2));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(3));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(4));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(5));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(6));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(7));
//                BookMarkUtil.AddVideoToBookMark("감사",Util.getAllVideos().get(8));

//                BookMarkUtil.AddBookMark("테스트1");
//                BookMarkUtil.AddVideoToBookMark("테스트1",Util.getAllVideos().get(0));
//                BookMarkUtil.AddVideoToBookMark("테스트1",Util.getAllVideos().get(1));
//                BookMarkUtil.AddVideoToBookMark("테스트1",Util.getAllVideos().get(2));

                //BookMarkUtil.DeleteVideoFromBookMark("테스트1", Util.getAllVideos().get(2));

//                if(BookMarkUtil.AddBookMark("테스트1")){
//                    refresh();
//                }
//                if(BookMarkUtil.AddVideoToBookMark("미분류",Util.getAllVideos().get(2))){
//                    refresh();
//                }


            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof CardInfo) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.VIDEO_INFO, (VideoInfo) ((CardInfo) item).getObject("videoInfo"));
                /*
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((LiveCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();*/
                getActivity().startActivity(intent /*, bundle*/);
            }

            if (item instanceof VideoInfo) {
                VideoInfo videoInfo = (VideoInfo) item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.VIDEO_INFO, videoInfo);
                /*
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((LiveCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                        */
                getActivity().startActivity(intent /*, bundle*/);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {

                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            Log.e("MainFragment", "itemSelected");
            currentItem = item;
            /*
            if (item instanceof Movie) {
                mBackgroundUri = ((Movie) item).getBackgroundImageUrl();
                //startBackgroundTimer();
            }*/
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private void refresh() {
        loadRows();
    }
}
