package com.tyomsky.empublite;

import android.net.Uri;

import java.io.File;
import java.util.List;

public class BookContents {
    String title;
    List<BookContents.Chapter> chapters;
    File baseDir;

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public int getChapterCount() {
        return chapters.size();
    }

    public String getChapterFile(int position) {
        return chapters.get(position).file;
    }

    String getChapterPath(int position) {
        String file=getChapterFile(position);
        if (baseDir == null) {
            return("file:///android_asset/book/" + file);
        }
        return(Uri.fromFile(new File(baseDir, file)).toString());
    }

    public String getTitle() {
        return title;
    }

    public String getChapterTitle(int position) {
        return chapters.get(position).title;
    }

    static class Chapter {
        String file;
        String title;
    }
}
