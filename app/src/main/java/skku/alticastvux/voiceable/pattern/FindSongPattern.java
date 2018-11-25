package skku.alticastvux.voiceable.pattern;

import com.alticast.mmuxclient.ClientAPI;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by woorim on 2018. 7. 23..
 */

public class FindSongPattern extends VoiceablePattern {

    private static String PATTERN = "^(\\S+)?(노래|음악|배경음악|브금)(가수)?(검색(해줘)?|찾아(줘)?|알려줘|뭐야|뭐냐|뭐여)?$";

    public static boolean matches(String response) {
        return Pattern.matches(PATTERN, response.replaceAll(" ",""));
    }

    @Override
    public void parse(String response) {
        //do nothing
    }
}
