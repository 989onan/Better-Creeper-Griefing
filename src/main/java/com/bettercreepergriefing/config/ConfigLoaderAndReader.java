package com.bettercreepergriefing.config;

import java.io.File;

import com.bettercreepergriefing.MainBetterCreeperGriefing;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MainBetterCreeperGriefing.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConfigLoaderAndReader {
	//making config loader and builders
	public static Builder configbuilder = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec config;
	
	static{
		init(configbuilder);
		config = configbuilder.build();
	}
	//building config
	public static void loadConfig(ForgeConfigSpec config, String path) {
		MainBetterCreeperGriefing.LOGGER.info("Loading Configs at: "+path);
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		MainBetterCreeperGriefing.LOGGER.info("Built config at: "+path);
		file.load();
		MainBetterCreeperGriefing.LOGGER.info("Loaded config at: "+path);
		config.setConfig(file);
		
	}
	//initializing varibles that will have the values in the config file
	public static ForgeConfigSpec.DoubleValue damagemultiplier;
	
	//loading values into varibles
	public static void init(ForgeConfigSpec.Builder serverconfig) {
		serverconfig.comment("Better Creeper Griefing Config");
		
		damagemultiplier = serverconfig.comment("Damage multiplier (multiplies the damage done within the explosion radius. Takes into account the distance.) This is set at default 2 times the damage.").defineInRange("bettercreepergriefing.damagemultiplier", 2.0D, 0.0D, 10000.0D);

	}
}