package skku.alticastvux.voiceable.pattern;

import java.util.regex.Pattern;

/**
 * Created by woorim on 2018. 7. 31..
 */

public class AddBookMarkPattern extends VoiceablePattern {

    public static boolean matches(String response) {
        return Pattern.matches(".+가", response);
    }

    @Override
    public void parse(String response) {

    }
}
