package eu.darkcode.event;

public enum EventCategory {
    ATTRIBUTE, EFFECT, MOVEMENT, VISUAL, DEFAULT;

    public boolean canHaveMultipleAtTime() {
        return this != DEFAULT;
    }
}