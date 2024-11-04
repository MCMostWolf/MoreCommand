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

public class AbsorptionCommand {
    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("absorption").requires((player) -> {
                    int level = HealthCommandConfig.permissionLevel.get();
                    return player.hasPermission(level);
                }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((source) -> addAbsorption(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount")))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((source) -> setAbsorption(EntityArgument.getEntities(source, "targets"),
                                IntegerArgumentType.getInteger(source, "amount"))))))
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> getAbsorption(source.getSource(), EntityArgument.getEntities(source, "targets"))))));
    }

    private static int addAbsorption(Collection<? extends Entity> targets, int amount) {
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
            livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + amount);
        }
        return i;
    }

    private static int setAbsorption(Collection<? extends Entity> targets, int amount) {
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
            livingEntity.setAbsorptionAmount(amount);
        }
        return i;
    }

    private static int getAbsorption(CommandSource source, Collection<? extends Entity> targets) {
        int absorptionValue=0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            source.sendSuccess(
                    new StringTextComponent(
                            livingEntity.getName().getString() + "有" + livingEntity.getAbsorptionAmount() + "额外护盾（黄心）"),
                    false);
            absorptionValue=(int)livingEntity.getAbsorptionAmount();
        }
        return absorptionValue;
    }
}
