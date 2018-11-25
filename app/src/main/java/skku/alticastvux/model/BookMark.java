package skku.alticastvux.model;

/**
 * Created by woorim on 2018. 7. 25..
 */

public class BookMark {
    private long id; //video id
    private long videoid;
    private long time; // milliseconds
    private int category = 0;
    private String description;
    transient private VideoInfo videoInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public long getVideoId() {
        return videoid;
    }

    public void setVideoId(long videoid) {
        this.videoid = videoid;
    }
}
