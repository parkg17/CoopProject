package skku.alticastvux.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.widget.Toast;

import skku.alticastvux.R;
import skku.alticastvux.activity.BookmarkActivity;
import skku.alticastvux.activity.DetailsActivity;
import skku.alticastvux.activity.MainActivity;
import skku.alticastvux.activity.PlaybackActivity;
import skku.alticastvux.model.BookCategory;
import skku.alticastvux.model.BookMark;
import skku.alticastvux.model.Genre;
import skku.alticastvux.model.VideoInfo;
import skku.alticastvux.presenter.BookmarkPresenter;
import skku.alticastvux.presenter.CardInfo;
import skku.alticastvux.presenter.CardPresenterSelector;
import skku.alticastvux.util.DBUtil;
import skku.alticastvux.util.Util;
import skku.alticastvux.util.VideoBackgroundManager;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;
import skku.alticastvux.widget.LiveCardView;

/**
 * Created by woorim on 2018. 7. 31..
 */

import java.util.ArrayList;
import java.util.HashMap;

public class BookmarkFragment extends BrowseFragment implements CommandListener {

    VideoBackgroundManager videoBackgroundManager;
    private ArrayObjectAdapter mRowsAdapter;
    private BookMark currentItem;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BookmarkActivity) getActivity()).setCommandListener(this);

        ArrayList<VideoInfo> videoInfos = Util.getAllVideos();

        setTitle("북마크");
        setupUIElements();
        loadRows();
        videoBackgroundManager = new VideoBackgroundManager(getActivity().getWindow());
        videoBackgroundManager.setVideoPath(videoInfos.get((int) (Math.random() * videoInfos.size())).getPath());
        videoBackgroundManager.startVideo();
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        BookmarkPresenter presenter = new BookmarkPresenter();

        ArrayList<BookMark> bookMarks = DBUtil.getInstance().getAllBookMarks();
        HashMap<Integer, ArrayList<BookMark>> map = new HashMap();
        ArrayList<BookCategory> bookCategories = DBUtil.getInstance().getBookCategories();

        for (BookMark b : bookMarks) {
            if (map.get(b.getCategory()) == null) {
                map.put(b.getCategory(), new ArrayList<BookMark>());
            }
            map.get(b.getCategory()).add(b);
        }

        for (BookCategory c : bookCategories) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
            ArrayList<BookMark> bookMarkList = map.get(c.getId());
            if (bookMarkList == null) continue;
            Log.e(getClass().getSimpleName() + " " + c.getId(), bookMarkList.size() + "");
            for (BookMark b : bookMarkList) {
                b.setVideoInfo(DBUtil.getInstance().getVideoWithId(b.getVideoId()));
                Log.e(getClass().getSimpleName() + " " + c.getId(), "add bookmark " + b.getVideoInfo().getName());
                listRowAdapter.add(b);
            }

            HeaderItem header = new HeaderItem(c.getId(), c.getName());
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }
        setAdapter(mRowsAdapter);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle("북마크"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
        setOnItemViewClickedListener(new BookmarkFragment.ItemViewClickedListener());
        // setOnItemViewSelectedListener(new BookmarkFragment.ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof BookMark) {
                BookMark bookMark = (BookMark) item;
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                intent.putExtra(DetailsActivity.VIDEO_INFO, bookMark.getVideoInfo());
                intent.putExtra("TIME", bookMark.getTime());
                getActivity().startActivity(intent);
            }
        }
    }


    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        return false;
    }
}
