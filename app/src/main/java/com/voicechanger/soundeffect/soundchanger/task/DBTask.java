package com.voicechanger.soundeffect.soundchanger.task;

import android.os.AsyncTask;
import android.util.Log;

public class DBTask extends AsyncTask<Void, Void, Void> {
    private IDBTaskListener mDownloadListener;

    public DBTask(IDBTaskListener iDBTaskListener) {
        this.mDownloadListener = iDBTaskListener;
    }

    @Override
    protected Void doInBackground(Void... voidArr) {

        if (this.mDownloadListener != null) {
            this.mDownloadListener.onDoInBackground();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voidR) {
        if (this.mDownloadListener != null) {
            this.mDownloadListener.onPostExcute();
        }
    }

    @Override
    protected void onPreExecute() {
        if (this.mDownloadListener != null) {
            this.mDownloadListener.onPreExcute();
        }
    }
}
