package com.tyomsky.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.tyomsky.empublite.event.BookLoadedEvent;
import de.greenrobot.event.EventBus;
import io.karim.MaterialTabs;

public class EmPubLiteActivity extends Activity {

    private ViewPager pager;
    private ContentsAdapter adapter;
    private static final String MODEL = "model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupStrictMode();

        setContentView(R.layout.main);
        pager = (ViewPager) findViewById(R.id.pager);
    }

    private void setupPager(BookContents contents) {
        adapter = new ContentsAdapter(this, contents);
        pager.setAdapter(adapter);
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if (adapter == null) {
            ModelFragment mFragment = (ModelFragment) getFragmentManager().findFragmentByTag(MODEL);
            if (mFragment == null) {
                getFragmentManager().beginTransaction().add(new ModelFragment(), MODEL).commit();
            } else if (mFragment.getBook() != null) {
                setupPager(mFragment.getBook());
            }
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                pager.setCurrentItem(0, false);
                return true;
            case R.id.about:
                i = new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/about.html");
                startActivity(i);
                return true;
            case R.id.help:
                i = new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/help.html");
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(BookLoadedEvent event) {
        setupPager(event.getBook());
    }

    private void setupStrictMode() {
        StrictMode.ThreadPolicy.Builder builder=
                new StrictMode.ThreadPolicy.Builder().detectNetwork();
        if (BuildConfig.DEBUG) {
            builder.penaltyDeath();
        }
        else {
            builder.penaltyLog();
        }
        StrictMode.setThreadPolicy(builder.build());
    }
}
