package skku.alticastvux.voiceable.pattern;

/**
 * Created by woorim on 2018. 7. 30..
 */

import java.util.regex.Pattern;

public class AddGenrePattern extends VoiceablePattern {

    private String genreName;

    public static boolean matches(String response) {
        return Pattern.matches("카테고리.+추가", response);
    }
    @Override
    public void parse(String response) {
        genreName = response.substring(4,response.length()-2).trim();
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
