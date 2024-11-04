package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BackCommand {
    //建立哈希表
    private static final Map<Entity, Vector3d> deathPositions = new HashMap<>();
    private static final Map<Entity, String> deathDimensions = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        //监听玩家死亡事件
            LivingEntity player =event.getEntityLiving();
            deathPositions.put(player, player.position());
            deathDimensions.put(player, player.level.dimension().location().toString());
    }

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("back")
                .requires((player) -> player.hasPermission(HealthCommandConfig.permissionLevel.get()))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("froms", EntityArgument.entities())
                                .executes(context -> backLastDiePos(EntityArgument.getEntities(context, "targets"), EntityArgument.getEntities(context, "froms"))))));
    }

    private static int backLastDiePos(Collection<? extends Entity> targets, Collection<? extends Entity> froms) {
        int count = 0;
        for (Entity targetEntity : targets) {
            if (targetEntity instanceof LivingEntity targetLivingEntity) {
                for (Entity fromEntity : froms) {
                    Vector3d deathPos = deathPositions.get(fromEntity);
                    String deathDimension=deathDimensions.get(fromEntity);
                    if (deathPos != null) {
                        MinecraftServer server = targetLivingEntity.getServer();
                        CommandSource commandSource = targetLivingEntity.createCommandSourceStack();
                        try {
                            String command = "execute in " + deathDimension + " run tp " + deathPos.x + " " + deathPos.y + " " +deathPos.z;
                            if (server != null) {
                                server.getCommands().performCommand(commandSource,command);
                            }
                            else {
                                targetLivingEntity.sendMessage(new StringTextComponent(targetEntity.getName().getString()+ "未知错误"), targetLivingEntity.getUUID());
                            }
                            } catch (Exception ignored){}
                            count++;
                            }
                        if (deathPos == null) {
                            targetLivingEntity.sendMessage(new StringTextComponent(targetEntity.getName().getString()+ "没有上一次死亡信息"), targetLivingEntity.getUUID());
                        }
                }
            }
        }
        return count;
    }

}