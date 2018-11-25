package skku.alticastvux.voiceable;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alticast.mmuxclient.ClientAPI;

import java.util.ArrayList;

import skku.alticastvux.activity.MainActivity;
import skku.alticastvux.voiceable.pattern.AddBookMarkPattern;
import skku.alticastvux.voiceable.pattern.AddGenrePattern;
import skku.alticastvux.voiceable.pattern.ChangeGenrePattern;
import skku.alticastvux.voiceable.pattern.DeliveryPattern;
import skku.alticastvux.voiceable.pattern.FindFoodPattern;
import skku.alticastvux.voiceable.pattern.FindMenuPattern;
import skku.alticastvux.voiceable.pattern.FindSongPattern;
import skku.alticastvux.voiceable.pattern.FindStorePattern;
import skku.alticastvux.voiceable.pattern.MovePattern;
import skku.alticastvux.voiceable.pattern.RefreshPattern;
import skku.alticastvux.voiceable.pattern.SelectPattern;
import skku.alticastvux.voiceable.pattern.ShowDetailPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;


/**
 * Created by dy.yoon on 2018-05-18.
 */

public class ASREventController implements ClientAPI.Callback<ClientAPI.ASRResult> {
    private ClientAPI.ASRContext asrContext = null;
    private CommandListener listener = null;

    private Context context = null;
    private MainGrammar grammar = null;

    public static ASREventController getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ASREventController instance = new ASREventController();
    }

    @Override
    public void callback(ClientAPI.ASRResult asrResult) {
        String response = asrResult.getSpokenResponse();
        Log.e(getClass().getSimpleName(), response);
        String pattern = asrResult.getMatchedPattern();
        ArrayList<ClientAPI.Entity> entities = asrResult.getMatchedEntities();

        if (listener != null) {
            VoiceablePattern vp = null;
            if (FindSongPattern.matches(response)) {
                vp = new FindSongPattern();
            } else if (MovePattern.matches(response)) {
                vp = new MovePattern();
                vp.parse(response);
            } else if(AddGenrePattern.matches(response)) {
                vp = new AddGenrePattern();
                vp.parse(response);
            } else if(RefreshPattern.matches(response)) {
                vp = new RefreshPattern();
            } else if(ChangeGenrePattern.matches(response)) {
                vp = new ChangeGenrePattern();
                vp.parse(response);
            } else if(AddBookMarkPattern.matches(response)) {
                vp = new AddBookMarkPattern();
                vp.parse(response);
            } else if(ShowDetailPattern.matches(response)) {
                vp = new ShowDetailPattern();
                vp.parse(response);
            } else if(DeliveryPattern.matches(response)) {
                vp = new DeliveryPattern();
            } else if(FindMenuPattern.matches(response)) {
                vp = new FindMenuPattern();
                vp.parse(response);
            } else if(FindStorePattern.matches(response)) {
                vp = new FindMenuPattern();
                vp.parse(response);
            } else if(FindFoodPattern.matches(response)) {
                vp = new FindMenuPattern();
                vp.parse(response);
            }
            if (vp != null)
                Log.e(getClass().getSimpleName(), "Result : " + vp.getClass().getSimpleName());
            listener.receiveCommand(vp);
        }
    }

    public void setCommandListener(CommandListener listener, String sceneName) {
        if (grammar != null)
            resetGrammar(grammar, sceneName);
        this.listener = listener;
    }

    public void removeCommandListener(CommandListener listener) {
        if (this.listener != null && this.listener.equals(listener))
            this.listener = null;
    }

    public void createASRContext(final Context context) {
        this.context = context;
        asrContext = ClientAPI.createASRContext(context, "Main", new ClientAPI.Callback<Bundle>() {
            @Override
            public void callback(Bundle bundle) {
                grammar = new MainGrammar();
                resetGrammar(grammar, MainActivity.class.getSimpleName());
                openEventPipe();
            }
        });
    }

    public void destroyASRContext() {
        if (asrContext != null) {
            asrContext.enableScreenContext();
            ClientAPI.destroyASRContext(asrContext);
        }
        if (context != null) {
            ClientAPI.unbind(context);
        }
    }

    public void resetGrammar(final MainGrammar grammar, final String sceneName) {
        asrContext.clearASRPatterns(
                new ClientAPI.Callback<ClientAPI.ASRResult>() {
                    @Override
                    public void callback(ClientAPI.ASRResult asrResult) {
                        for (String pattern : grammar.getPatterns(sceneName)) {
                            asrContext.addASRPattern(pattern, ASREventController.this);
                        }
                    }
                }
        );

        for (String pattern : grammar.getPatterns(sceneName)) {
            asrContext.addASRPattern(pattern, ASREventController.this);
        }
    }

    public void clearASRPatterns() {
        if (asrContext == null) return;
        asrContext.clearASRPatterns(new ClientAPI.Callback<ClientAPI.ASRResult>() {
            @Override
            public void callback(ClientAPI.ASRResult asrResult) {
                // Callback Body
            }
        });
    }

    public void showVoicePrompt() {
        if (asrContext == null) return;
        asrContext.showVoicePrompt();

    }

    public void hideVoicePrompt() {
        if (asrContext == null) return;
        asrContext.hideVoicePrompt();
    }

    public void openEventPipe() {
        if (asrContext == null) return;
        asrContext.openEventPipe();
        registerAPIEvents();
    }

    public void registerAPIEvents() {
        ClientAPI.Callback<ClientAPI.ClientEvent> callback = new ClientAPI.Callback<ClientAPI.ClientEvent>() {
            @Override
            public void callback(ClientAPI.ClientEvent event) {
                event.performAction();
            }
        };
        asrContext.registerHandledEvents("scrollDown", callback);
        asrContext.registerHandledEvents("scrollLeft", callback);
        asrContext.registerHandledEvents("scrollRight", callback);
        asrContext.registerHandledEvents("scrollUp", callback);
    }
}