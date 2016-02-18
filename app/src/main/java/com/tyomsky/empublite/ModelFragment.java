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
import com.tyomsky.empublite.event.BookUpdatedEvent;
import com.tyomsky.empublite.service.DownloadCheckService;
import de.greenrobot.event.EventBus;

import java.io.*;

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

        EventBus.getDefault().register(this);
        if (contents == null) {
            new LoadThread(context).start();
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    public void onEventBackgroundThread(BookUpdatedEvent event) {
        if (getActivity() != null) {
            new LoadThread(getActivity()).start();
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

            File baseDir=
                    new File(context.getFilesDir(),
                            DownloadCheckService.UPDATE_BASEDIR);
            try {
                InputStream is;
                if (baseDir.exists()) {
                    is=new FileInputStream(new File(baseDir, "contents.json"));
                }
                else {
                    is=context.getAssets().open("book/contents.json");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                contents=gson.fromJson(reader, BookContents.class);
                is.close();
                if (baseDir.exists()) {
                    contents.setBaseDir(baseDir);
                }
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
