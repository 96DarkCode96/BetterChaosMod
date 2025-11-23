package eu.darkcode.event;

public enum EventServerType {
    SINGLEPLAYER, MULTIPLAYER, BOTH;

    /**
     * @param isDedicatedServer whether the environment is a dedicated server
     */
    public boolean match(boolean isDedicatedServer) {
        return switch (this) {
            case BOTH -> true;
            case SINGLEPLAYER -> !isDedicatedServer;   // integrated server
            case MULTIPLAYER -> isDedicatedServer;     // dedicated server
        };
    }
}
