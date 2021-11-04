package omrecorder;

import android.content.ContentValues;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;


import com.voicechanger.soundeffect.soundchanger.AudioApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

final class Wav extends AbstractRecorder {
    private final RandomAccessFile wavFile;
   // private RecordFileManagerImpl recordFileManagerIml = new RecordFileManagerImpl(MyApplication.appContext);

    public Wav(PullTransport pullTransport, File file) {
        super(pullTransport, file);
        this.wavFile = randomAccessFile(file);
    }

    private RandomAccessFile randomAccessFile(File file) {
        try {
            return new RandomAccessFile(file, "rw");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void writeWavHeader() {
        long size = 0;
        try {
            size = new FileInputStream(this.b).getChannel().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.wavFile.seek(0);
            this.wavFile.write(new WavHeader(this.a.source(), size).toBytes());
            this.wavFile.close();
//            RecordFileRoomDatabase.databaseWriteExecutor.execute(() -> {
//                recordFileManagerIml.save(b.getPath());
//            });
           // insertToMediaStore(this.b);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        super.stopRecording();
        writeWavHeader();
    }

    private void insertToMediaStore(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            final ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav");
            values.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.SIZE, file.length());

            AudioApplication.appContext.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        }
    }
}
