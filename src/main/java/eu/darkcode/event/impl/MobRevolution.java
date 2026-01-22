package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.InstantEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


/**
 * @author _dark_code_
 * @date 02.01.2026
 **/
public class MobRevolution extends InstantEvent {

    public static final @NotNull EventRegistry.Entry<?> ENTRY = EventRegistry.entry("mob_revolution", MobRevolution::new).build();

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }

    @Override
    public void start(@NotNull EventManager manager) {
        manager.activePlayers().forEach(player -> {
            Vec3 position = player.position();
            AABB area = new AABB(position.add(-80, -80, -80), position.add(80, 80, 80));
            player.level().getEntities(player, area).forEach(entity -> {
                if (!(entity instanceof PathfinderMob mob))
                    return;
                mob.setTarget(player);
                AttributeMap attributes = mob.getAttributes();
                attributes.attributes.computeIfAbsent(Attributes.ATTACK_DAMAGE, a -> {
                    AttributeInstance attributeInstance = new AttributeInstance(a, b -> {
                    });
                    attributeInstance.setBaseValue(2);
                    return attributeInstance;
                });
                AttributeInstance followRange = attributes.attributes.computeIfAbsent(Attributes.FOLLOW_RANGE, a -> new AttributeInstance(a, b -> {
                }));
                if (followRange.getBaseValue() < 80)
                    followRange.setBaseValue(80);
                if (!(mob instanceof RangedAttackMob) && !(mob instanceof Warden) && mob.goalSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal)
                        .noneMatch(goal -> goal instanceof MeleeAttackGoal || goal instanceof SwellGoal))
                    mob.goalSelector.addGoal(-1, new MeleeAttackGoal(mob, 1.15, true));
            });
        });
    }
}
