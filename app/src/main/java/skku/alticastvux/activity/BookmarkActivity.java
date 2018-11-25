package skku.alticastvux.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import skku.alticastvux.R;
import skku.alticastvux.activity.base.BaseActivity;
import skku.alticastvux.activity.base.BaseFragmentActivity;
import skku.alticastvux.model.VideoInfo;
import skku.alticastvux.util.Util;
import skku.alticastvux.voiceable.ASREventController;

/**
 * Created by woorim on 2018. 7. 31..
 */

public class BookmarkActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ASREventController.getInstance().createASRContext(getApplicationContext());
        setContentView(R.layout.activity_bookmark);
    }
}
