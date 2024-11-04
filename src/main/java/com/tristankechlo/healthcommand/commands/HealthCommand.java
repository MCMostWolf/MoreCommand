package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;

public class HealthCommand {

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("health").requires((player) -> {
                    int level = HealthCommandConfig.permissionLevel.get();
                    return player.hasPermission(level);
                }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((source) -> addHealth(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount")))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((source) -> setHealth(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount"))))))
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> getHealth(source.getSource(), EntityArgument.getEntities(source, "targets")))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> resetHealth(source.getSource(), EntityArgument.getEntities(source, "targets"))))));
        HealthCommandMain.LOGGER.debug(HealthCommandMain.MOD_ID + ": Health command registered");
    }

    private static int addHealth(Collection<? extends Entity> targets, int amount) {
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
            if (setHealthSingle(livingEntity, livingEntity.getHealth() + amount)) {
                i++;
            }
        }
        return i;
    }

    private static int setHealth(Collection<? extends Entity> targets, int amount) {
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
            if (setHealthSingle(livingEntity, amount)) {
                i++;
            }
        }
        return i;
    }

    private static int getHealth(CommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            float health = livingEntity.getHealth();
            source.sendSuccess(new StringTextComponent(livingEntity.getName().getString() + " has " + health + " health left."), false);
        }
        return 1;
    }

    private static int resetHealth(CommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            final float health = livingEntity.getMaxHealth();
            livingEntity.setHealth(health);
            source.sendSuccess(new StringTextComponent("Resetted the health of " + livingEntity.getName().getString()), false);
        }
        return 1;
    }

    private static boolean setHealthSingle(LivingEntity livingEntity, float newHealth) {
        livingEntity.setHealth(Math.min(newHealth, livingEntity.getMaxHealth()));
        return true;
    }

}
