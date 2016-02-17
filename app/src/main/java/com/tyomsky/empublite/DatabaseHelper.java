package com.tyomsky.empublite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import com.tyomsky.empublite.event.NoteLoadedEvent;
import de.greenrobot.event.EventBus;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "empublite.db";
    public static final int SCHEMA_VERSION = 1;
    public static DatabaseHelper singleton;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new DatabaseHelper(context);
        }

        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("This should not be called");
    }

    void loadNote(int position) {
        new LoadThread(position).start();
    }

    void updateNote(int position, String prose) {
        new UpdateThread(position, prose).start();
    }

    private class LoadThread extends Thread {
        private int position = -1;

        public LoadThread(int position) {
            super();
            this.position = position;
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            String[] args = {String.valueOf(position)};
            Cursor c = getReadableDatabase().rawQuery("SELECT prose FROM notes WHERE position = ?",
                    args);
            if (c.getCount()>0) {
                c.moveToFirst();
                EventBus.getDefault().post(new NoteLoadedEvent(position,
                        c.getString(0)));
            }
            c.close();
        }
    }

    private class UpdateThread extends Thread{
        private int position=-1;
        private String prose;

        UpdateThread(int position, String prose) {
            super();
            this.position = position;
            this.prose = prose;
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            String[] args = {String.valueOf(position), String.valueOf(prose)};
            getWritableDatabase().execSQL("INSERT OR REPLACE INTO notes (position, prose) VALUES (?, ?)", args);
        }
    }

}
