package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.*;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(HealthCommandMain.MOD_ID)
public class HealthCommandMain {

    public static final String MOD_NAME = "HealthCommand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final String MOD_ID = "healthcommand";

    public HealthCommandMain() {
        // register commands
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        // setup configs
        MinecraftForge.EVENT_BUS.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(BackCommand::onPlayerDeath);


    }

    private void registerCommands(final RegisterCommandsEvent event) {
        HealthCommand.register(event.getDispatcher());
        AbsorptionCommand.register(event.getDispatcher());
        DamageCommand.register(event.getDispatcher());
        FireCommand.register(event.getDispatcher());
        BackCommand.register(event.getDispatcher());

    }

    private void commonSetup(final FMLServerAboutToStartEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

}