package eu.darkcode.client.voting.integration;

/**
 * @author _dark_code_
 * @date 14.02.2026
 **/
public abstract class AbstractPlatformSettings {
    public String token;
    public String refreshToken;
    public long tokenExpiry = -1L;
}
