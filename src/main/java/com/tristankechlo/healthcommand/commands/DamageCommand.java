package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import java.util.Collection;

public class DamageCommand {
    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("damage").requires((player) -> {
            int level = HealthCommandConfig.permissionLevel.get();
            return player.hasPermission(level);
        }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .then(Commands.argument("froms", EntityArgument.entities())
                                .executes((source) -> addDamage(
                                        EntityArgument.getEntities(source, "targets"),
                                        IntegerArgumentType.getInteger(source, "amount"),
                                        EntityArgument.getEntities(source, "froms"))))))));
    }

    private static int addDamage(Collection<? extends Entity> targets, int amount, Collection<? extends Entity> froms) {
        int i = 0;
        for (Entity targetEntity : targets) {
            if (!(targetEntity instanceof LivingEntity)) {
                continue;
            }
            if (targetEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) targetEntity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            LivingEntity targetLivingEntity = (LivingEntity) targetEntity;
            for (Entity damageSourceEntity : froms) {
                if (damageSourceEntity instanceof LivingEntity) {
                    LivingEntity damageSourceLivingEntity = (LivingEntity) damageSourceEntity;
                    targetLivingEntity.hurt(DamageSource.mobAttack(damageSourceLivingEntity), amount);
                    i++;
                }
            }
        }
        return i;
    }
}