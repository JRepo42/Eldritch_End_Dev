package elocindev.eldritch_end.config;

import elocindev.eldritch_end.EldritchEnd;
import elocindev.eldritch_end.config.entries.ClientConfig;
import elocindev.eldritch_end.config.entries.CorruptionConfig;
import elocindev.eldritch_end.config.entries.biomes.HasturianWastesConfig;
import elocindev.eldritch_end.config.entries.biomes.PrimordialAbyssConfig;
import elocindev.eldritch_end.config.entries.entities.AberrationConfig;
import elocindev.eldritch_end.config.entries.entities.DendlerConfig;
import elocindev.eldritch_end.config.entries.entities.HasturConfig;
import elocindev.eldritch_end.config.entries.entities.TentacleConfig;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ConfigLoader {

    public static void register() {
        NecConfigAPI.registerConfig(CorruptionConfig.class);
        NecConfigAPI.registerConfig(PrimordialAbyssConfig.class);
        NecConfigAPI.registerConfig(HasturianWastesConfig.class);
        NecConfigAPI.registerConfig(AberrationConfig.class);
        NecConfigAPI.registerConfig(TentacleConfig.class);
        NecConfigAPI.registerConfig(DendlerConfig.class);

        NecConfigAPI.registerConfig(HasturConfig.class);
    }

    public static void initDatapack(boolean started) {
        
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success)
		->  {
			if (started) {
                register();
			}
		});
		
        EldritchEnd.LOGGER.info("Eldritch End's Config Loaded!");
    }

    public static void initClient() {
        NecConfigAPI.registerConfig(ClientConfig.class);
        Configs.Client.CLIENT_CONFIG = ClientConfig.INSTANCE;
        
        EldritchEnd.LOGGER.info("Eldritch End's Client Config Loaded!");
    }
}