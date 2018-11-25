package skku.alticastvux.voiceable.pattern;

/**
 * Created by woorim on 2018. 7. 30..
 */

import java.util.HashMap;

public class SelectPattern extends VoiceablePattern {

    public static HashMap<String, OnSelectListener> map;

    public void removeAll() {
        map.clear();
    }

    public void add(String tag, OnSelectListener l) {
        map.put(tag, l);
    }

    public interface OnSelectListener {
        public void onSelect(String response);
    }

    @Override
    public void parse(String response) {

    }

    public static boolean matches(String response) {
        return false;
    }

}
