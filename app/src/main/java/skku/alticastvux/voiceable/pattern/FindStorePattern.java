package skku.alticastvux.voiceable.pattern;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by gyeol on 2018. 11. 25.
 */

public class FindStorePattern extends VoiceablePattern {

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    private String store;

    private static String PATTERN = "^(\\S+)?[선택]$";

    public static boolean matches(String response) {
        return Pattern.matches(PATTERN, response.replaceAll(" ",""));
    }

    @Override
    public void parse(String response) {
        response = response.replaceAll(" ","");
        store = response.replaceAll("[선택]", "");
    }
}
