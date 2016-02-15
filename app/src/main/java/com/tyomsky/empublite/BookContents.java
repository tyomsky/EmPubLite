package com.tyomsky.empublite;

import java.util.List;

public class BookContents {
    String title;
    List<BookContents.Chapter> chapters;

    public int getChapterCount() {
        return chapters.size();
    }

    public String getChapterFile(int position) {
        return chapters.get(position).file;
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
