package eu.darkcode.event;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EventTest {

    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testDisplayName() {
        EventRegistry.EVENTS.stream().forEach(event -> {
            Event construct = event.construct();
            Component component = Assertions.assertDoesNotThrow(construct::displayName, "Display name for " + event.clazz().getName() + " failed!");
            Assertions.assertNotNull(component);
        });
    }

}