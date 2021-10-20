package com.voicechanger.soundeffect.soundchanger.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voicechanger.soundeffect.soundchanger.Common;
import com.voicechanger.soundeffect.soundchanger.dialog.ConfirmDialog;
import com.voicechanger.soundeffect.soundchanger.dialog.ConfirmSaveAudioDialog;
import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.constants.IVoiceChangerConstants;
import com.voicechanger.soundeffect.soundchanger.dialog.LoadingDialog;
import com.voicerecorder.axet.audiolibrary.trimmer.customAudioViews.MarkerView;
import com.voicerecorder.axet.audiolibrary.trimmer.customAudioViews.SamplePlayer;
import com.voicerecorder.axet.audiolibrary.trimmer.customAudioViews.SoundFile;
import com.voicerecorder.axet.audiolibrary.trimmer.customAudioViews.WaveformView;
import com.voicerecorder.axet.audiolibrary.trimmer.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TrimAudioActivity extends AppCompatActivity implements View.OnClickListener,
        MarkerView.MarkerListener, WaveformView.WaveformListener {

    private static final int REQUEST_STORAGE_PERMISSION = 102;
    private MarkerView markerStart, markerEnd;
    private WaveformView audioWaveform;
    private TextView txtStartPosition, txtEndPosition, txtAudioTitle;
    private Button btnAudioCancel, btnReset, btnCut;
    private ImageView imgAudioPlay, imgNext, imgPrev, ivZoomIn, ivZoomOut;

    private boolean isAudioRecording = false;
    private boolean mRecordingKeepGoing;
    private SoundFile mLoadedSoundFile;
    private SoundFile mAudioFile;
    private SamplePlayer mPlayer;
    private Handler mHandler;

    private boolean mTouchDragging;
    private float mTouchStart;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private int mTouchInitialEndPos;
    private float mDensity;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMarkerBottomOffset;
    private int mPlayStartMsec;
    private int mPlayEndMillSec;

    private int mOffset;
    private int mOffsetGoal;
    private int mFlingVelocity;
    private int mWidth;
    private int mMaxPos;
    private int mStartPos;
    private int mEndPos;

    private boolean mStartVisible;
    private boolean mEndVisible;
    private int mLastDisplayedStartPos;
    private int mLastDisplayedEndPos;
    private boolean mIsPlaying = false;
    private boolean mKeyDown;
    private ProgressDialog savingRecordDialog;
    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private File mFile;
    private ConfirmSaveAudioDialog confirmSaveAudioDialog;
    private ConfirmDialog confirmDialog;
    private ImageView ivBack;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim_audio);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_bold));
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_back);

        mHandler = new Handler(Looper.getMainLooper());

        btnAudioCancel = findViewById(R.id.btnAudioCancel);
//        txtAudioUpload = findViewById(R.id.txtAudioUpload);
        txtAudioTitle = findViewById(R.id.txtAudioTitle);
        txtStartPosition = findViewById(R.id.txtStartPosition);
        txtEndPosition = findViewById(R.id.txtEndPosition);
        markerStart = findViewById(R.id.markerStart);
        markerEnd = findViewById(R.id.markerEnd);
        audioWaveform = findViewById(R.id.audioWaveform);
//        txtAudioRecordTimeUpdate = findViewById(R.id.txtAudioRecordTimeUpdate);
        btnReset = findViewById(R.id.btnReset);
        imgAudioPlay = findViewById(R.id.imgAudioPlay);
        imgNext = findViewById(R.id.imgNext);
        imgPrev = findViewById(R.id.imgPrev);
        btnCut = findViewById(R.id.btnCut);
        ivZoomIn = findViewById(R.id.ivZoomIn);
        ivZoomOut = findViewById(R.id.ivZoomOut);
        ivBack = findViewById(R.id.ivBack);

        mAudioFile = null;
        mKeyDown = false;
        audioWaveform.setListener(this);
        markerStart.setListener(this);
//        markerStart.setAlpha(1f);
        markerStart.setFocusable(true);
        markerStart.setFocusableInTouchMode(true);
        mStartVisible = true;

        markerEnd.setListener(this);
//        markerEnd.setAlpha(1f);
        markerEnd.setFocusable(true);
        markerEnd.setFocusableInTouchMode(true);
        mEndVisible = true;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        /**
         * Change this for marker handle as per your view
         */
//        mMarkerLeftInset = (int) (30 * mDensity);
        mMarkerLeftInset = (int) markerStart.getPaddingStart();
        mMarkerRightInset = (int) (30 * mDensity);
        mMarkerTopOffset = (int) (6 * mDensity);
        mMarkerBottomOffset = (int) (6 * mDensity);

        /**
         * Change this for duration text as per your view
         */

//        mTextLeftInset = (int) (20 * mDensity);
//        mTextTopOffset = (int) (-1 * mDensity);
//        mTextRightInset = (int) (19 * mDensity);
//        mTextBottomOffset = (int) (-40 * mDensity);

        btnAudioCancel.setOnClickListener(this);
//        txtAudioUpload.setOnClickListener(this);
        imgAudioPlay.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        btnCut.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        ivZoomIn.setOnClickListener(this);
        ivZoomOut.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        mHandler.postDelayed(mTimerRunnable, 100);
        ProgressDialog openAudioDialog = new ProgressDialog(this);
        openAudioDialog.setMessage("Loading...");
        openAudioDialog.show();
        showTrimmingEditor(openAudioDialog);
    }

//    @Override
//    protected void onDestroy() {
//        if (mPlayer != null) mPlayer.release();
//        super.onDestroy();
//    }

    @Override
    protected void onPause() {
        if (mPlayer != null) {
            handlePause();
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnCut.performClick();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        showAdsAndFinish();
        super.onBackPressed();
    }

    private void showAdsAndFinish() {
//        if (System.currentTimeMillis() - AdmodUtils.getInstance().lastTimeShowInterstitial > 30 * 1000) {
//            AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(this, Common.getRemoteConfigAdUnit("ads_admob_inter_back_home"), 0,
//                    new AdCallback() {
//                        @Override
//                        public void onAdClosed() {
//                            finish();
//                        }
//
//                        @Override
//                        public void onAdFail() {
//                            finish();
//                        }
//                    }, true);
//        } else {
//            finish();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating Text is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos) {
                txtStartPosition.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos) {
                txtEndPosition.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view == btnAudioCancel) {
            onBackPressed();
            //showAdsAndFinish();
        } else if (view == imgAudioPlay) {
            if (!mIsPlaying) {
                imgAudioPlay.setImageResource(R.drawable.ic_pause_circle);
            } else {
                imgAudioPlay.setImageResource(R.drawable.ic_backward);
            }
            onPlay(mStartPos);
        } else if (view == btnCut) {
            if (mIsPlaying) handlePause();
            if (Utility.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION)) {
                showSaveAudioDialog();
            }
        } else if (view == btnReset) {
            if (mIsPlaying) handlePause();
            audioWaveform.setIsDrawBorder(true);
            mPlayer = new SamplePlayer(mAudioFile);
            finishOpeningSoundFile(mAudioFile);
        } else if (view == imgNext) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMillSec)
                    newPos = mPlayEndMillSec;
                mPlayer.seekTo(newPos);
            } else {
                markerEnd.requestFocus();
//                mEndMarker.setImageResource(R.drawable.ic_keo2);
//                mStartMarker.setImageResource(R.drawable.ic_keo1);
                markerFocus(markerEnd);
            }
        } else if (view == imgPrev) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                markerStart.requestFocus();
//                mStartMarker.setImageResource(R.drawable.ic_keo1);
//                mEndMarker.setImageResource(R.drawable.ic_keo2);
                markerFocus(markerStart);
            }
        }
        switch (view.getId()){
            case R.id.ivZoomIn:
                waveformZoomIn();
                break;
            case R.id.ivZoomOut:
                waveformZoomOut();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void showSaveAudioDialog() {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        final View dialogView = inflater.inflate(R.layout.alert_dialog_save_trimmed_audio, null);
//        final AlertDialog renameDialog = new AlertDialog.Builder(this).create();
//        renameDialog.setView(dialogView);
//
//        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
//        Button btnOk = dialogView.findViewById(R.id.btnOk);
//        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
//
//        Objects.requireNonNull(btnOk).setOnClickListener(v -> {
//            saveAudio(edtTitle.getText().toString());
//            renameDialog.dismiss();
//        });
//
//        Objects.requireNonNull(btnCancel).setOnClickListener(v -> renameDialog.cancel());
//
//        renameDialog.show();
        confirmSaveAudioDialog = new ConfirmSaveAudioDialog(this, new ConfirmSaveAudioDialog.CallBackConfirmSaveDialogListener() {
            @Override
            public void onSelectSkip() {
                confirmSaveAudioDialog.dismiss();
            }

            @Override
            public void onSelectOk(String name) {
                saveAudio(name);
                confirmSaveAudioDialog.dismiss();
            }
        });
        confirmSaveAudioDialog.show();
        Window window = confirmSaveAudioDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

//    private void updateAudioTitle(String title) {
//        runOnUiThread(() -> txtAudioTitle.setText(title));
//    }

    private void saveAudio(String title) {
        double startTime = audioWaveform.pixelsToSeconds(mStartPos);
        double endTime = audioWaveform.pixelsToSeconds(mEndPos);
        double trimmingLength = endTime - startTime;
        if (trimmingLength <= 0) {
            Toast.makeText(this, getString(R.string.trim_sec_alert), Toast.LENGTH_SHORT).show();
        } else {
            // Create an indeterminate progress dialog
//            savingRecordDialog = new ProgressDialog(this);
//            savingRecordDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            savingRecordDialog.setTitle("Processing....");
//            savingRecordDialog.setIndeterminate(true);
//            savingRecordDialog.setCancelable(false);
//            savingRecordDialog.show();

            loadingDialog = new LoadingDialog(this ,R.style.full_screen_dialog);
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            Window window = loadingDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            // Try AAC first.
            String fileName = title + IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_M4A;
            File parentDirFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");

            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

//            parentDir = shared.getString(MainApplication.PREFERENCE_STORAGE, parentDir);

            // Create the parent directory if not exist
            if (!parentDirFile.exists()) parentDirFile.mkdirs();
//
            File outFile = new File(parentDirFile.getPath() + File.separator + fileName);
            Runnable runnable = () -> {
                afterSavingRingtone(title, outFile.getPath(), (int) Math.round(endTime - startTime));
                audioWaveform.setIsDrawBorder(true);
            };
            if (outFile.exists()) {
                showExistingFileDialog(outFile, runnable);
            } else {
                startSavingAudio(outFile, runnable);
            }
        }
    }

    private void startSavingAudio(File outFile, Runnable runnable) {
        // Save the sound file in a background thread
        new Thread() {
            @Override
            public void run() {
                double startTime = audioWaveform.pixelsToSeconds(mStartPos);
                double endTime = audioWaveform.pixelsToSeconds(mEndPos);
                final int startFrame = audioWaveform.secondsToFrames(startTime);
//                final int endFrame = audioWaveform.secondsToFrames(endTime - 0.04);
                final int endFrame = audioWaveform.secondsToFrames(endTime);

                try {
                    mAudioFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                    mHandler.post(runnable);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(TrimAudioActivity.this, getString(R.string.save_audio_failed), Toast.LENGTH_SHORT).show());
                } finally {
                    loadingDialog.dismiss();
                }
            }
        }.start();
    }

    private void showExistingFileDialog(File outFile, Runnable runnable) {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        final View dialogView = inflater.inflate(R.layout.alert_dialog_override_file, null);
//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setView(dialogView);
//
//        Button btnOk = dialogView.findViewById(R.id.btnOk);
//        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
//
//        Objects.requireNonNull(btnOk).setOnClickListener(v -> {
//            outFile.delete();
//            startSavingAudio(outFile, runnable);
//            alertDialog.dismiss();
//            savingRecordDialog.dismiss();
//        });
//
//        Objects.requireNonNull(btnCancel).setOnClickListener(v -> {
//            alertDialog.cancel();
//            savingRecordDialog.dismiss();
//        });
//
//        alertDialog.show();
        confirmDialog = new ConfirmDialog(this, new ConfirmDialog.CallBackConfirmDialogListener() {
            @Override
            public void onSelectOk() {

            }

            @Override
            public void onSelectCancel() {

            }
        }, "", getResources().getString(R.string.override_file));
        confirmDialog.show();
        Window window = confirmDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Start recording
     *
     * @param openAudioDialog
     */
    private void showTrimmingEditor(ProgressDialog openAudioDialog) {
        Bundle bundle = getIntent().getExtras();
        String pathAudio = bundle.getString(Common.EXTRA_AUDIO_URI);
        new Thread() {
            @Override
            public void run() {
                isAudioRecording = false;
                mRecordingKeepGoing = false;
                try {
                    mAudioFile = SoundFile.create(pathAudio, null);
                    //updateAudioTitle(FilenameUtils.getName(uri.getPath()));
                } catch (IOException | SoundFile.InvalidInputException e) {
                    e.printStackTrace();
                }
                if (mAudioFile != null) {
                    mPlayer = new SamplePlayer(mAudioFile);
                }
                audioWaveform.setIsDrawBorder(true);
                runOnUiThread(() -> {
                    finishOpeningSoundFile(mAudioFile);
                    openAudioDialog.dismiss();
                });
            }
        }.start();
    }

    /**
     * After recording finish do necessary steps
     *
     * @param mSoundFile sound file
     */
    private void finishOpeningSoundFile(SoundFile mSoundFile) {
        audioWaveform.setSoundFile(mSoundFile);
        audioWaveform.recomputeHeights(mDensity);

        mMaxPos = audioWaveform.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos)
            mEndPos = mMaxPos;

//        if (isReset == 1) {
//            mStartPos = audioWaveform.secondsToPixels(0);
//            mEndPos = audioWaveform.secondsToPixels(audioWaveform.pixelsToSeconds(mMaxPos));
//        }

//        if (audioWaveform != null && audioWaveform.isInitialized()) {
//            double seconds = audioWaveform.pixelsToSeconds(mMaxPos);
//            txtAudioRecordTimeUpdate.setText(formatDecimal(seconds));
//        }
        updateDisplay();
    }

    /**
     * Update views
     */

    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition();
            int frames = audioWaveform.millisecsToPixels(now);
            audioWaveform.setPlayback(frames);
            double startTimePlay = mPlayer.getCurrentPosition() * 1.0 / 1000;
            txtStartPosition.setText(formatTime(audioWaveform.secondsToPixels(startTimePlay)));
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMillSec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        audioWaveform.setParameters(mStartPos, mEndPos, mOffset);
        audioWaveform.invalidate();

        markerStart.setContentDescription(" Start Marker" + formatTime(mStartPos));
        markerEnd.setContentDescription(" End Marker" + formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
//        int startX = mStartPos - mOffset;
        if (startX + markerStart.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(() -> {
                    mStartVisible = true;
                    markerStart.setAlpha(1f);
                }, 0);
            }
        } else {
            if (mStartVisible) {
                markerStart.setAlpha(0f);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - markerEnd.getWidth() + mMarkerLeftInset;
//        int endX = mEndPos - mOffset - markerEnd.getWidth();
        if (endX + markerEnd.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(() -> {
                    mEndVisible = true;
                    markerEnd.setAlpha(1f);
                }, 0);
            }
        } else {
            if (mEndVisible) {
                markerEnd.setAlpha(0f);
                mEndVisible = false;
            }
            endX = 0;
        }



        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(startX , audioWaveform.getMeasuredHeight() + markerEnd.getHeight() / 2 - mMarkerLeftInset * 2, 0, 0);
        markerStart.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(endX , audioWaveform.getMeasuredHeight() + markerEnd.getHeight() / 2 - mMarkerLeftInset * 2, 0, 0);
        markerEnd.setLayoutParams(params);
    }

    /**
     * Reset all positions
     */

    private void resetPositions() {
        mStartPos = audioWaveform.secondsToPixels(0.0);
        mEndPos = audioWaveform.secondsToPixels(30.0);
        updateDisplay();
    }

    private void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    private String formatTime(int pixels) {
        if (audioWaveform != null && audioWaveform.isInitialized()) {
            return formatDecimal(audioWaveform.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    private String formatDecimal(double seconds) {
        //* seconds = 102.34 then time is 102sec : 34 millis
        long millis = (long) seconds * 1000; //* convert to milliseconds
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
        handlePause();
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == markerStart) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == markerStart) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == markerStart) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == markerEnd) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == markerStart) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == markerEnd) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == markerStart) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(this::updateDisplay, 100);
    }

//
// WaveformListener
//

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    public void waveformDraw() {
        mWidth = audioWaveform.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
//        long mWaveformTouchStartMsec = Utility.getCurrentTime();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
//        /*mTouchDragging = false;
//        mOffsetGoal = mOffset;
//
//        long elapsedMsec = Utility.getCurrentTime() - mWaveformTouchStartMsec;
//        if (elapsedMsec < 300) {
//            if (mIsPlaying) {
//                int seekMsec = audioWaveform.pixelsToMillisecs(
//                        (int) (mTouchStart + mOffset));
//                if (seekMsec >= mPlayStartMsec &&
//                        seekMsec < mPlayEndMillSec) {
//                    mPlayer.seekTo(seekMsec);
//                } else {
////                    handlePause();
//                }
//            } else {
//                onPlay((int) (mTouchStart + mOffset));
//            }
//        }
    }

    private synchronized void handlePause() {
        imgAudioPlay.setImageResource(R.drawable.ic_play_circle);
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        audioWaveform.setPlayback(-1);
        mIsPlaying = false;
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = audioWaveform.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mEndPos);
            }
            mPlayer.setOnCompletionListener(this::handlePause);
            mIsPlaying = true;

            mPlayer.seekTo(mPlayStartMsec);
            mPlayer.start();
            updateDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void waveformZoomIn() {
        audioWaveform.zoomIn();
        mStartPos = audioWaveform.getStart();
        mEndPos = audioWaveform.getEnd();
        mMaxPos = audioWaveform.maxPos();
        mOffset = audioWaveform.getOffset();
        mOffsetGoal = mOffset;
        updateDisplay();
    }

    public void waveformZoomOut() {
        audioWaveform.zoomOut();
        mStartPos = audioWaveform.getStart();
        mEndPos = audioWaveform.getEnd();
        mMaxPos = audioWaveform.maxPos();
        mOffset = audioWaveform.getOffset();
        mOffsetGoal = mOffset;
        updateDisplay();
    }

    /**
     * After saving as ringtone set its content values
     *
     * @param title    title
     * @param outPath  output path
     * @param duration duration of file
     */
    private void afterSavingRingtone(CharSequence title, String outPath, int duration) {
//        File outFile = new File(outPath);
//        long fileSize = outFile.length();
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DATA, outPath);
//        values.put(MediaStore.MediaColumns.TITLE, title.toString());
//        values.put(MediaStore.MediaColumns.SIZE, fileSize);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, Utility.AUDIO_MIME_TYPE);
//        values.put(MediaStore.Audio.Media.ARTIST, getApplicationInfo().name);
//        values.put(MediaStore.Audio.Media.DURATION, duration);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

//        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
//        final Uri newUri = getContentResolver().insert(uri, values);
//        Log.e("final URI >> ", newUri + " >> " + outPath);

//        Bundle conData = new Bundle();
//        conData.putString("INTENT_AUDIO_FILE", outPath);
//        Intent intent = getIntent();
//        intent.putExtras(conData);
//        setResult(RESULT_OK, intent);
        Toast.makeText(this, getString(R.string.saved_to) + outPath, Toast.LENGTH_LONG).show();
        //updateAudioTitle(FilenameUtils.getName(outPath));
        loadFromFile(outPath);
    }

    /**
     * Load file from path
     *
     * @param mFilename file name
     */
    private void loadFromFile(String mFilename) {
        mFile = new File(mFilename);
        mLoadingLastUpdateTime = Utility.getCurrentTime();
        mLoadingKeepGoing = true;
        savingRecordDialog = new ProgressDialog(this);
        savingRecordDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        savingRecordDialog.setTitle("Opening cut file...");
        savingRecordDialog.show();

        final SoundFile.ProgressListener listener =
                fractionComplete -> {
                    long now = Utility.getCurrentTime();
                    if (now - mLoadingLastUpdateTime > 100) {
                        savingRecordDialog.setProgress(
                                (int) (savingRecordDialog.getMax() * fractionComplete));
                        mLoadingLastUpdateTime = now;
                    }
                    return mLoadingKeepGoing;
                };

        // Load the sound file in a background thread
        Thread mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mLoadedSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
                    if (mLoadedSoundFile == null) {
                        savingRecordDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = "No Extension";
                        } else {
                            err = "Bad Extension";
                        }
                        final String finalErr = err;
                        Log.e(" >> ", "" + finalErr);
                        return;
                    }
                    mPlayer = new SamplePlayer(mLoadedSoundFile);
                } catch (final Exception e) {
                    savingRecordDialog.dismiss();
                    e.printStackTrace();
                    return;
                }
                savingRecordDialog.dismiss();
                if (mLoadingKeepGoing) {
                    Runnable runnable = () -> {
//                            audioWaveform.setVisibility(View.INVISIBLE);
//                            audioWaveform.setBackgroundColor(getResources().getColor(R.color.audio_cut_color));
                        audioWaveform.setIsDrawBorder(false);
                        mAudioFile = mLoadedSoundFile;
                        finishOpeningSoundFile(mLoadedSoundFile);
                    };
                    mHandler.post(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }
}
