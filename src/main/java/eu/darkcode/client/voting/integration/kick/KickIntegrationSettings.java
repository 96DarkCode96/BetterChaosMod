package eu.darkcode.client.voting.integration.kick;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KickIntegrationSettings {
    public String token;
    public String scope;
    public boolean announceNewPolls = true;
    public String refreshToken;
    public long tokenExpiry = -1L;
    public String kickPusherKey = "32cbd69e4b950bf97679";
    public String kickPusherCluster = "us2";
    public int chatroomId = -1;
}
