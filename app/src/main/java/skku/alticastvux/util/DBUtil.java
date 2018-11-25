package skku.alticastvux.util;

import android.icu.util.ULocale;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.reflect.TypeToken;

import skku.alticastvux.model.BookCategory;
import skku.alticastvux.model.BookMark;
import skku.alticastvux.model.Genre;
import skku.alticastvux.model.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by woorim on 2018. 7. 30..
 */

public class DBUtil {
    private static DBUtil instance;

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    private ArrayList<Genre> genres;
    private ArrayList<BookCategory> bookCategories;

    public ArrayList<BookCategory> getBookCategories() {
        return bookCategories;
    }

    private Map<String, ArrayList<VideoInfo>> videoMap; //genre id, videos
    private Map<Long, VideoInfo> videoMapWithId; //video id
    private Map<String, ArrayList<BookMark>> bookmarkMap; // video id, bookmarks

    private Gson gson;

    public static DBUtil getInstance() {
        if(instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }

    private DBUtil() {
        gson = new Gson();
        loadGenre();
        loadVideoMap();
        loadBookMarkMap();
        loadCategory();
    }

    public void addVideos(int i, ArrayList<VideoInfo> videoInfos) {
        ArrayList<VideoInfo> vs = videoMap.get(String.valueOf(i));
        if(vs == null) {
            videoMap.put(String.valueOf(i), new ArrayList<VideoInfo>());
            vs = videoMap.get(String.valueOf(i));
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (VideoInfo v : vs) {
            ids.add(v.getId());
        }
        for (VideoInfo v : videoInfos) {
            if(!ids.contains(v.getId())) {
                v.setGenre(i);
                addVideoInfo(i, v);
            }
        }
    }

    public ArrayList<VideoInfo> getVideos(int i) {
        if(videoMap.get(String.valueOf(i)) == null) {
            videoMap.put(String.valueOf(i), new ArrayList<VideoInfo>());
        }
        return videoMap.get(String.valueOf(i));
    }

    public void loadGenre() {
        String pref = SharedPreferencesUtil.getString("genres", "");
        if (pref.length() == 0) {
            genres = new ArrayList<>();
            addGenre("기본");
        } else {
            genres = gson.fromJson(pref, new TypeToken<ArrayList<Genre>>() {}.getType());
        }
    }

    public void loadCategory() {
        String pref = SharedPreferencesUtil.getString("categories", "");
        if (pref.length() == 0) {
            bookCategories = new ArrayList<>();
            addCategory("기본");
        } else {
            bookCategories = gson.fromJson(pref, new TypeToken<ArrayList<BookCategory>>() {}.getType());
        }
    }


    public void saveCategory() {
        SharedPreferencesUtil.putString("categories", gson.toJson(bookCategories));
    }

    public BookCategory addCategory(String s) {
        BookCategory c = new BookCategory(bookCategories.size(), s);
        bookCategories.add(c);
        saveCategory();
        return c;
    }

    public void saveGenre() {
        SharedPreferencesUtil.putString("genres", gson.toJson(genres));
    }

    public Genre addGenre(String s) {
        Genre genre = new Genre(genres.size(), s);
        genres.add(genre);
        saveGenre();
        return genre;
    }

    public void removeGenre(Genre genre) {
        if (genre.getId() == 0) return;
        if (videoMap.get(genre.getId()) != null) {
            ArrayList<VideoInfo> videoInfos = videoMap.get(genre.getId());
            while (videoInfos.size() > 0) {
                addVideoInfo(0, videoInfos.get(0));
                videoInfos.remove(0);
            }
        }
    }

    public VideoInfo getVideoWithId(Long id) {
        return videoMapWithId.get(id);
    }

    public void loadVideoMap() {
        videoMapWithId = new HashMap<Long, VideoInfo>();
        String pref = SharedPreferencesUtil.getString("videoMap", "");
        Log.e("DBUtil", pref);
        if (pref.length() == 0) {
            videoMap = new LinkedHashTreeMap<>();
        } else {
            videoMap = new Gson().fromJson(pref, new TypeToken<Map<String, ArrayList<VideoInfo>>>() {}.getType());
            for(String s : videoMap.keySet()) {
                for(VideoInfo v: videoMap.get(s)) {
                    videoMapWithId.put(v.getId(), v);
                }
            }
        }
    }

    public void loadBookMarkMap() {
        String pref = SharedPreferencesUtil.getString("bookMarkMap", "");
        if (pref.length() == 0) {
            bookmarkMap = new LinkedHashTreeMap<>();
        } else {
            bookmarkMap = new Gson().fromJson(pref, new TypeToken<Map<String, ArrayList<BookMark>>>() {}.getType());
        }
    }

    public ArrayList<BookMark> getAllBookMarks() {
        ArrayList<BookMark> bookMarks = new ArrayList<BookMark>();
        for (String s: bookmarkMap.keySet()) {
            for(BookMark b: bookmarkMap.get(s)) {
                bookMarks.add(b);
            }
        }
        return bookMarks;
    }

    public ArrayList<BookMark> getBookMarkList(int i) {
        if(bookmarkMap.get(String.valueOf(i)) == null) {
            bookmarkMap.put(String.valueOf(i), new ArrayList<BookMark>());
        }
        return bookmarkMap.get(String.valueOf(i));
    }

    // 북마크 카테고리
    public void addBookMark(int i, BookMark b) {
        b.setVideoId(i);
        if(bookmarkMap.get(String.valueOf(i)) == null) {
            bookmarkMap.put(String.valueOf(i), new ArrayList<BookMark>());
        }
        bookmarkMap.get(String.valueOf(i)).add(b);
        SharedPreferencesUtil.putString("bookMarkMap", gson.toJson(bookmarkMap));
    }

    public void addVideoInfo(int i, VideoInfo info) {
        if (videoMap.get(String.valueOf(i)) == null) {
            videoMap.put(String.valueOf(i), new ArrayList<VideoInfo>());
        }
        info.setGenre(i);
        videoMapWithId.put(info.getId(), info);
        videoMap.get(String.valueOf(i)).add(info);
        SharedPreferencesUtil.putString("videoMap", gson.toJson(videoMap));
    }

    public void removeVideoInfo(int i, VideoInfo info) {
        if (videoMap.get(String.valueOf(i)) == null) {
            videoMap.put(String.valueOf(i), new ArrayList<VideoInfo>());
        }
        videoMap.get(String.valueOf(i)).remove(info);
        SharedPreferencesUtil.putString("videoMap", gson.toJson(videoMap));
    }


}
