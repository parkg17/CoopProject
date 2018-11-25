package skku.alticastvux.voiceable.pattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gyeol on 2018. 11. 25.
 */

public class DeliveryPattern extends VoiceablePattern {

    //private static final String PATTERN = "[음식](주문)[할래|해[줘]]";
    private static final String PATTERN = "(주문)";

    public static boolean matches(String response) {
        return Pattern.matches(PATTERN, response.replace(" ",""));
    }

    @Override
    public void parse(String response) {
        //do nothing
    }
}
