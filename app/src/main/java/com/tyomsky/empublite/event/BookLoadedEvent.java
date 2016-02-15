package com.tyomsky.empublite.event;

import com.tyomsky.empublite.BookContents;

public class BookLoadedEvent {
    private BookContents contents = null;

    public BookLoadedEvent(BookContents contents) {
        this.contents = contents;
    }

    public BookContents getBook() {
        return contents;
    }
}
