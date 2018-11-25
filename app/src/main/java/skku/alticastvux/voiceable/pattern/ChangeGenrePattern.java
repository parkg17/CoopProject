package skku.alticastvux.voiceable.pattern;

import android.util.Log;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import skku.alticastvux.model.Genre;
import skku.alticastvux.util.DBUtil;

/**
 * Created by woorim on 2018. 7. 30..
 */

public class ChangeGenrePattern extends VoiceablePattern {

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    private String genre;

    public static boolean matches(String response) {
        response = response.replaceAll(" ","");
        if(!Pattern.matches("(.+)(으로|에다?|로)?(|추가|옮겨줘|변경)", response)) {
            return false;
        } else {
            String s = ".+(으로|로|에다?)?(|추가|옮겨줘|변경)";
            Pattern pattern = Pattern.compile(s);
            Matcher matcher = pattern.matcher("영화에다추가");
            String genre = response.replaceAll("(으?로?|(에다?))?(추가|옮겨줘|변경)", "").replaceAll(" ","");
            for(Genre g: DBUtil.getInstance().getGenres()) {
                if(g.getName().equals(genre)) {
                    Log.e("sss","compare : "+g.getName()+" "+genre);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void parse(String response) {
        response = response.replaceAll(" ","");
        String s = ".+(으로|로|에다?)?(|추가|옮겨줘|변경)";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher("영화에다추가");
        genre = response.replaceAll("(으?로?|(에다?))?(추가|옮겨줘|변경)", "");

    }
}
