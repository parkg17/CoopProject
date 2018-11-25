package skku.alticastvux.voiceable.pattern;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by gyeol on 2018. 11. 25.
 */

public class FindFoodPattern extends VoiceablePattern {

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    private String food;

    private static String PATTERN = "^(\\S+)?[선택]$";

    public static boolean matches(String response) {
        return Pattern.matches(PATTERN, response.replaceAll(" ",""));
    }

    @Override
    public void parse(String response) {
        response = response.replaceAll(" ","");
        food = response.replaceAll("[선택]", "");
    }
}
