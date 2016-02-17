package com.tyomsky.empublite.event;

public class NoteLoadedEvent {

    int position;
    String prose;

    public NoteLoadedEvent(int position, String prose) {
        this.position = position;
        this.prose = prose;
    }

    public int getPosition() {
        return position;
    }

    public String getProse() {
        return prose;
    }
}
