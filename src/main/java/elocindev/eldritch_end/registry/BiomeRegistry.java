package elocindev.eldritch_end.registry;

import elocindev.eldritch_end.config.Configs;
import elocindev.eldritch_end.worldgen.biome.PrimordialAbyss;
import elocindev.eldritch_end.EldritchEnd;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BiomeRegistry {
    public static final RegistryKey<Biome> PRIMORDIAL_ABYSS = RegistryKey.of(Registry.BIOME_KEY, 
        new Identifier(EldritchEnd.MODID, "primordial_abyss"));

    public static void register() {
        PrimordialAbyss.register();

        TheEndBiomes.addHighlandsBiome(PRIMORDIAL_ABYSS, Configs.BIOME_PRIMORDIAL_ABYSS.biome_weight);
        TheEndBiomes.addMidlandsBiome(PRIMORDIAL_ABYSS, PRIMORDIAL_ABYSS, Configs.BIOME_PRIMORDIAL_ABYSS.biome_weight);

        PrimordialAbyss.registerModifications();
    }
}
