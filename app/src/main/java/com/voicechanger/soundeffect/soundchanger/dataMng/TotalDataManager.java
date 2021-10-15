package com.voicechanger.soundeffect.soundchanger.dataMng;

import com.voicechanger.soundeffect.soundchanger.constants.IVoiceChangerConstants;
import com.voicechanger.soundeffect.soundchanger.object.EffectObject;
import com.voicechanger.soundeffect.soundchanger.soundMng.SoundManager;

import java.util.ArrayList;
import java.util.Iterator;

public class TotalDataManager implements IVoiceChangerConstants {
    public static final String TAG = "TotalDataManager";
    private static TotalDataManager totalDataManager;
    private ArrayList<EffectObject> listEffectObjects;

    private TotalDataManager() {
    }

    public static TotalDataManager getInstance() {
        if (totalDataManager == null) {
            totalDataManager = new TotalDataManager();
        }
        return totalDataManager;
    }

    public ArrayList<EffectObject> getListEffectObjects() {
        return this.listEffectObjects;
    }

    public void onDestroy() {
        if (this.listEffectObjects != null) {
            this.listEffectObjects.clear();
            this.listEffectObjects = null;
        }
        try {
            SoundManager.getInstance().releaseSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalDataManager = null;
    }

    public void onResetState() {
        if (this.listEffectObjects != null && this.listEffectObjects.size() > 0) {
            Iterator it = this.listEffectObjects.iterator();
            while (it.hasNext()) {
                ((EffectObject) it.next()).setPlaying(false);
            }
        }
    }

    public void setListEffectObjects(ArrayList<EffectObject> arrayList) {
        this.listEffectObjects = arrayList;
    }
}
