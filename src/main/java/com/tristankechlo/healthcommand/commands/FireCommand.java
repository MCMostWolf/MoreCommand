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
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;

public class FireCommand {
    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("fire").requires((player) -> {
                    int level = HealthCommandConfig.permissionLevel.get();
                    return player.hasPermission(level);
                }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((source) -> addFire(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount")))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((source) -> setFire(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount"))))))
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> getFire(source.getSource(), EntityArgument.getEntities(source, "targets"))))));
    }

    private static int addFire(Collection<? extends Entity> targets, int amount) {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setRemainingFireTicks(livingEntity.getRemainingFireTicks() + amount);
        }
        return i;
    }

    private static int setFire(Collection<? extends Entity> targets, int amount) {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setRemainingFireTicks(amount);
        }
        return i;
    }

    private static int getFire(CommandSource source, Collection<? extends Entity> targets) {
        int FireTick = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            source.sendSuccess(
                    new StringTextComponent(
                            livingEntity.getName().getString() + "还有" + ((double)livingEntity.getRemainingFireTicks())/20 + "秒着火时间"),
                    false);
            FireTick = livingEntity.getRemainingFireTicks();
        }

        return FireTick;
    }
}
