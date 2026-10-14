package eu.darkcode.client.voting.integration.kick;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author _dark_code_
 * @date 14.10.2026
 **/
class KickHelperTest {

    @Test
    void fetchChannelRoomId() {
        Integer channelRoomId = assertDoesNotThrow(() -> KickHelper.fetchChannelRoomId("96DarkCode96"));
        assertNotNull(channelRoomId);
        assertEquals(33002279, channelRoomId);
    }

    @Test
    void getClientId() {
        String clientId = assertDoesNotThrow(KickHelper::getClientId);
        assertNotNull(clientId);
    }

    @Test
    void fetchWebSocketURL() {
        String clientId = KickHelper.getClientId();
        assertNotNull(clientId);

        String url = assertDoesNotThrow(() -> KickHelper.fetchWebSocketURL(clientId));
        assertNotNull(url);
    }

    @Test
    void fetchJWTToken() {
        String clientId = KickHelper.getClientId();
        assertNotNull(clientId);

        String jwtToken = assertDoesNotThrow(() -> KickHelper.fetchJWTToken(clientId));
        assertNotNull(jwtToken);
    }
}