package eu.darkcode.client.voting.integration.kick;

import eu.darkcode.client.voting.integration.AbstractPlatformSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KickIntegrationSettings extends AbstractPlatformSettings {
    public boolean announceNewPolls = true;
    public String kickPusherKey = "32cbd69e4b950bf97679";
    public String kickPusherCluster = "us2";
    public int chatroomId = -1;
}
