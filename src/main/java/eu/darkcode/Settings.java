package eu.darkcode;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class Settings {
    public static final StreamCodec<RegistryFriendlyByteBuf, Settings> CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, Settings settings) {
            buf.writeInt(settings.timerDuration);
            buf.writeInt(settings.eventDuration);
        }

        @Override
        public Settings decode(RegistryFriendlyByteBuf buf) {
            Settings settings = new Settings();
            settings.timerDuration = buf.readInt();
            settings.eventDuration = buf.readInt();
            return settings;
        }
    };
    public int timerDuration = 900; // 45s
    public int eventDuration = 600; // 30s
}
