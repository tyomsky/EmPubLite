package com.tyomsky.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

public class ContentsAdapter extends FragmentStatePagerAdapter {
    private BookContents contents;

    public ContentsAdapter(Activity context, BookContents contents) {
        super(context.getFragmentManager());
        this.contents = contents;
    }

    @Override
    public Fragment getItem(int position) {
        return(SimpleContentFragment.newInstance(contents.getChapterPath(position)));
    }

    @Override
    public int getCount() {
        return contents.getChapterCount();
    }

    @Override
    public String getPageTitle(int position) {
        return contents.getChapterTitle(position);
    }
}
