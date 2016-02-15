package com.tyomsky.empublite;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.tyomsky.empublite.event.BookLoadedEvent;
import de.greenrobot.event.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ModelFragment extends Fragment{

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
            new LoadThread(context.getAssets()).start();
        }
    }

    public BookContents getBook() {
        return contents;
    }

    private class LoadThread extends Thread {
        private AssetManager assets;

        public LoadThread(AssetManager assets) {
            super();
            this.assets=assets;
        }

        @Override
        public void run() {
            Gson gson = new Gson();

            try {
                InputStream is = assets.open("book/contents.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                contents=gson.fromJson(reader, BookContents.class);
                EventBus.getDefault().post(new BookLoadedEvent(contents));
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
            }
        }
    }

}
