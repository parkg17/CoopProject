package skku.alticastvux.voiceable;

import com.alticast.mmuxclient.ClientAPI;

import java.util.ArrayList;

import skku.alticastvux.voiceable.pattern.VoiceablePattern;

/**
 * Created by dy.yoon on 2018-05-18.
 */

public interface CommandListener {
    boolean receiveCommand(VoiceablePattern pattern);
}
