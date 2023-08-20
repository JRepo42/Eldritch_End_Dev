package elocindev.eldritch_end.worldgen.biome;

import elocindev.eldritch_end.config.Configs;
import elocindev.eldritch_end.registry.BiomeRegistry;
import elocindev.eldritch_end.registry.FeatureRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.EndPlacedFeatures;

public class HasturianWastes {
	public static void register() {
		Registry.register(BuiltinRegistries.BIOME, BiomeRegistry.HASTURIAN_WASTES.getValue(), createHasturianWastes());
	}
	
	private static Biome createHasturianWastes() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
				.feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_SPIKE);
		return compose(builder);
	}

	private static Biome compose(GenerationSettings.Builder builder) {
		SpawnSettings.Builder settings = new SpawnSettings.Builder();

		return (new Biome.Builder())
		.precipitation(Biome.Precipitation.NONE)
		.temperature(Configs.BIOME_PRIMORDIAL_ABYSS.biome_temperature)
		.downfall(0.1F)
		
		.effects((new BiomeEffects.Builder())
			.waterColor(2367016).waterFogColor(2949228).fogColor(2758197).skyColor(1312788)
			.build())

		.spawnSettings(settings.build())	
		.generationSettings(builder.build()).build();
	}

    public static void registerModifications() {
		BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(BiomeRegistry.HASTURIAN_WASTES),
            GenerationStep.Feature.LOCAL_MODIFICATIONS,
            RegistryKey.of(Registry.PLACED_FEATURE_KEY, FeatureRegistry.HASTURIAN_WASTES_SURFACE_ID)
        );

		BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(BiomeRegistry.HASTURIAN_WASTES),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            RegistryKey.of(Registry.PLACED_FEATURE_KEY, FeatureRegistry.HASTURIAN_SPIKES_ID)
        );
    }
}