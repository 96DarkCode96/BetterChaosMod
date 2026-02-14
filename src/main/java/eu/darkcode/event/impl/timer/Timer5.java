package eu.darkcode.event.impl.timer;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventCategory;
import eu.darkcode.event.EventRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author _dark_code_
 * @date 14.02.2026
 **/
public class Timer5 extends AbstractTimer {

    public static final EventRegistry.Entry<?> ENTRY = EventRegistry.entry("timer_5", Timer5::new).category(EventCategory.TIMER).build();

    protected Timer5() {
        super(5);
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}
