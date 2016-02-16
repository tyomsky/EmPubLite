package com.tyomsky.empublite;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.tyomsky.empublite.event.BookLoadedEvent;
import de.greenrobot.event.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ModelFragment extends Fragment{

    SharedPreferences prefs;
    private BookContents contents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (contents == null) {
            new LoadThread(context).start();
        }
    }

    public BookContents getBook() {
        return contents;
    }

    private class LoadThread extends Thread {
        private Context context;

        public LoadThread(Context context) {
            super();
            this.context=context;
        }

        @Override
        public void run() {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();

            try {
                InputStream is = context.getAssets().open("book/contents.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                contents=gson.fromJson(reader, BookContents.class);
                EventBus.getDefault().post(new BookLoadedEvent(contents));
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
            }
        }
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }
}
