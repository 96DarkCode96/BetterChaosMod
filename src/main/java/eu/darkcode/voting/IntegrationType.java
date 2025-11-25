package eu.darkcode.voting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public enum IntegrationType {
    TWITCH(Component.translatable("betterchaosmod.integration.twitch"));

    public static final StreamCodec<FriendlyByteBuf, EnumSet<IntegrationType>> CODEC = new StreamCodec<>() {
        @Override
        public EnumSet<IntegrationType> decode(FriendlyByteBuf byteBuf) {
            return byteBuf.readEnumSet(IntegrationType.class);
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, EnumSet<IntegrationType> set) {
            byteBuf.writeEnumSet(set, IntegrationType.class);
        }
    };
    
    private final @NotNull Component name;

    IntegrationType(@NotNull Component name) {
        this.name = name;
    }

    public @NotNull Component getName() {
        return name;
    }
}
