package eu.darkcode.event;

public enum EventCategory {
    ATTRIBUTE, EFFECT, MOVEMENT, VISUAL, SENSITIVITY, DEFAULT;

    public boolean canHaveMultipleAtTime() {
        return this != DEFAULT;
    }
}