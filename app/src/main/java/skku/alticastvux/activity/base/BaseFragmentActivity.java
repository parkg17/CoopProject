package skku.alticastvux.activity.base;

import android.support.v4.app.FragmentActivity;

import com.alticast.mmuxclient.ClientAPI;

import java.util.ArrayList;

import skku.alticastvux.voiceable.ASREventController;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;

/**
 * Created by woorim on 2018. 7. 20..
 */

public class BaseFragmentActivity extends FragmentActivity implements CommandListener {
    @Override
    protected void onResume() {
        super.onResume();
        ASREventController.getInstance().setCommandListener(this, this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ASREventController.getInstance().removeCommandListener(this);
    }

    CommandListener listener;

    public void setCommandListener(CommandListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        if (listener != null) return listener.receiveCommand(pattern);
        else return false;
    }
}
