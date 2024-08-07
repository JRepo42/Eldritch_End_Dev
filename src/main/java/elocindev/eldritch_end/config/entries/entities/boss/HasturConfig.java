package elocindev.eldritch_end.config.entries.entities.boss;

import elocindev.eldritch_end.config.ConfigFolder;
import elocindev.necronomicon.config.NecConfig;

public class HasturConfig {
    @NecConfig
    public static HasturConfig INSTANCE;

    public static String getFile() {
        return ConfigFolder.getNestedFile("hastur.json", "bosses");
    }

    public double HEALTH_ATTRIBUTE = 2500;
    public double MINION_SPAWN_AMOUNT = 3;
    public double HASTUR_OMNIVAMPIRISM_AMOUNT = 0.1;
    public double TENTACLE_CHANCE_PERSEC = 5;
    public double ENCARNATION_LIGHTNING_DAMAGE = 100;
    public boolean PLAYER_DYING_KILLS_OTHER_PLAYERS = true;
}
