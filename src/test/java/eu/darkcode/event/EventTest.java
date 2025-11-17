package eu.darkcode.event;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventTest {

    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testDisplayName() {
        EventRegistry.EVENTS.stream().forEach(event -> {
            Event construct = assertDoesNotThrow(event::construct);
        });
    }

}