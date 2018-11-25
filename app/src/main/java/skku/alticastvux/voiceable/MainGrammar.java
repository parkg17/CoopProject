package skku.alticastvux.voiceable;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;

import skku.alticastvux.voiceable.pattern.FindSongPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;


/**
 * Created by dy.yoon on 2018-05-18.
 */

public class MainGrammar {
    private final static String GLOBAL_SCENE = "_global";

    private MultiValuedMap<String, String> patternsMap = new ArrayListValuedHashMap<String, String>();
    private MultiValuedMap<String, String> exampleTextMap = new ArrayListValuedHashMap<String, String>();

    public MainGrammar() {
        registerPattern("${*query}", new String[]{GLOBAL_SCENE});
        // ${time} 동작안함
        /*
        for (int i = 0; i < Patterns.length; i++) {
            Class c = Patterns[i];
            try {
                VoiceablePattern pattern = (VoiceablePattern) c.getConstructor().newInstance(new Object[]{});
                registerPattern(pattern.getPattern(), new String[]{MainActivity.class.getSimpleName()});
            } catch (Exception e) {

            }
        }*/
    }


    private void registerPattern(String pattern, String[] screenNames) {
        for (String screenName : screenNames) {
            patternsMap.put(screenName, pattern);
        }
    }

    public String[] getPatterns(String screenName) {
        ArrayList result = new ArrayList(patternsMap.get(GLOBAL_SCENE));
        result.addAll(patternsMap.get(screenName));
        String[] resultStr = new String[result.size()];
        result.toArray(resultStr);
        return resultStr;
    }
}
