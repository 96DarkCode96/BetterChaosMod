package eu.darkcode.voting;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;

public enum IntegrationType {
    TWITCH(Component.translatable("betterchaosmod.integration.twitch")),
    KICK(Component.translatable("betterchaosmod.integration.kick")),
    ;

    public static final StreamCodec<FriendlyByteBuf, EnumSet<IntegrationType>> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull EnumSet<IntegrationType> decode(FriendlyByteBuf byteBuf) {
            return byteBuf.readEnumSet(IntegrationType.class);
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, @NonNull EnumSet<IntegrationType> set) {
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
