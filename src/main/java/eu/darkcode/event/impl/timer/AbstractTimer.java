package eu.darkcode.event.impl.timer;

import eu.darkcode.client.SharedProperties;
import eu.darkcode.event.CycleEvent;
import eu.darkcode.server.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 14.02.2026
 **/
public abstract class AbstractTimer extends CycleEvent {

    private final double multiplier;

    protected AbstractTimer(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public int duration() {
        return 3;
    }

    @Override
    public void end(@NotNull EventManager manager) {
        SharedProperties.timerDurationMultiplier = 1;
    }

    @Override
    public void endClient() {
        SharedProperties.timerDurationMultiplier = 1;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        SharedProperties.timerDurationMultiplier = multiplier;
    }

    @Override
    public void startClient() {
        SharedProperties.timerDurationMultiplier = multiplier;
    }
}