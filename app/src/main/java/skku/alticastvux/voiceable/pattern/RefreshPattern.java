package skku.alticastvux.voiceable.pattern;

/**
 * Created by woorim on 2018. 7. 30..
 */

public class RefreshPattern extends VoiceablePattern {

    public static boolean matches(String response) {
        return response.replaceAll(" ","").equals("새로고침");
    }
    @Override
    public void parse(String response) {

    }
}
