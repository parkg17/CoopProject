package skku.alticastvux.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import org.w3c.dom.Text;

import skku.alticastvux.model.VideoInfo;

/**
 * Created by woorim on 2018. 7. 25..
 */

public class CardPresenterSelector extends PresenterSelector {

    private Context mContext;

    public CardPresenterSelector(Context context) {
        mContext = context;
    }

    @Override
    public Presenter getPresenter(Object item) {
        return new CardPresenter();
        /*
                CardInfo cardInfo = (CardInfo) item;
        if (cardInfo.getCol() >= 1) {
            return new CardPresenter();
        } else {
            return new VideoTypePresenter();
        }
        */
    }
}
