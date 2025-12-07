package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.overlay.OverlayWebServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HudSettings {
    private int timerBackgroundColor = 0xFF000000;
    private int timerForegroundColor = 0xFF28A745;
    private int timerHeight = 12;
    private int activeEventsVerticalSpacing = 11;
    private int activeEventsMaxEntries = 8;
    private int pollWidth = 200;
    private int pollBarBackgroundColor = 0xFF009999;
    private int pollBarForegroundColor = 0xFFFFFFFF;

    public int pollWidth() {
        return pollWidth;
    }

    public void pollWidth(int pollWidth) {
        this.pollWidth = pollWidth;
        BetterChaosModClient.instance().hudManager().hud(CurrentPollPanel.class).recalculateEntries();
    }

    public int activeEventsMaxEntries() {
        return activeEventsMaxEntries;
    }

    public void activeEventsMaxEntries(int activeEventsMaxEntries) {
        this.activeEventsMaxEntries = activeEventsMaxEntries;
    }

    public int activeEventsVerticalSpacing() {
        return activeEventsVerticalSpacing;
    }

    public void activeEventsVerticalSpacing(int activeEventsVerticalSpacing) {
        this.activeEventsVerticalSpacing = activeEventsVerticalSpacing;
    }

    public int timerHeight() {
        return timerHeight;
    }

    public void timerHeight(int timerHeight) {
        this.timerHeight = timerHeight;
        OverlayWebServer.sendStyleUpdate();
    }

    public int timerForegroundColor() {
        return timerForegroundColor;
    }

    public void timerForegroundColor(int timerForegroundColor) {
        this.timerForegroundColor = timerForegroundColor;
    }

    public int timerBackgroundColor() {
        return timerBackgroundColor;
    }

    public void timerBackgroundColor(int timerBackgroundColor) {
        this.timerBackgroundColor = timerBackgroundColor;
    }

    public int pollBarForegroundColor() {
        return pollBarForegroundColor;
    }

    public void pollBarForegroundColor(int pollBarForegroundColor) {
        this.pollBarForegroundColor = pollBarForegroundColor;
    }

    public int pollBarBackgroundColor() {
        return pollBarBackgroundColor;
    }

    public void pollBarBackgroundColor(int pollBarBackgroundColor) {
        this.pollBarBackgroundColor = pollBarBackgroundColor;
    }
}
