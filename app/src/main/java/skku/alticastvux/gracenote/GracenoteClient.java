package skku.alticastvux.gracenote;

import android.content.Context;
import android.util.Log;

import com.gracenote.gnsdk.GnAudioFile;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLookupData;
import com.gracenote.gnsdk.GnLookupLocalStream;
import com.gracenote.gnsdk.GnLookupMode;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMusicIdStream;
import com.gracenote.gnsdk.GnMusicIdStreamPreset;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.gracenote.gnsdk.IGnAudioSource;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;

import java.io.File;

import skku.alticastvux.util.Util;


public class GracenoteClient {
    private GnManager gnManager;
    private GnUser gnUser;
    private GnMusicIdStream gnMusicIdStream;
    private IGnAudioSource gnAudioSource;
    private Context context;

    public GnUser getGnUser() {
        return gnUser;
    }

    public GracenoteClient(Context context) {
        this.context = context;
        try {
            gnManager = new GnManager(context, Util.getAssetAsString(context, GracenoteConstants.gnsdkLicenseFilename), GnLicenseInputMode.kLicenseInputModeString);
            gnUser = new GnUser(new GnUserStore(context), GracenoteConstants.gnsdkClientId, GracenoteConstants.gnsdkClientTag, GracenoteConstants.appString);
            GnStorageSqlite.enable();
            GnLookupLocalStream.enable();
        } catch (Exception e) {
            Log.e("GracenoteClient", "EXCEPTION!!");
            e.printStackTrace();
        }
    }

    public void findSong(String filename, IGnMusicIdStreamEvents events) {
        gnAudioSource = new AudioVisualizeAdapter(new GnAudioFile(new File(filename)));
        try {
            gnMusicIdStream = new GnMusicIdStream(gnUser, GnMusicIdStreamPreset.kPresetMicrophone, events);
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataContent, true);
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataSonicData, true);
            gnMusicIdStream.options().lookupMode(GnLookupMode.kLookupModeOnline);
            gnMusicIdStream.options().resultSingle(true);
            gnMusicIdStream.audioProcessStart(gnAudioSource);
            gnMusicIdStream.identifyAlbumAsync();
        } catch (Exception e) {
            Log.e("GracenoteClient", "EXCEPTION!!");
            e.printStackTrace();
        }
    }
}
