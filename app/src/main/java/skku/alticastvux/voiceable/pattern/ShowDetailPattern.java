package skku.alticastvux.voiceable.pattern;

/**
 * Created by woorim on 2018. 7. 31..
 */

public class ShowDetailPattern extends VoiceablePattern {

    public static boolean matches(String response) {
        return response.matches("자세히.+");
    }

    @Override
    public void parse(String response) {

    }
}
