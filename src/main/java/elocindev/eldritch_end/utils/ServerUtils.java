package elocindev.eldritch_end.utils;

import elocindev.eldritch_end.api.CorruptionAPI;
import elocindev.eldritch_end.config.Configs;
import elocindev.eldritch_end.corruption.corruption_effect.CEEyeSpawn;
import elocindev.eldritch_end.corruption.corruption_effect.CETentacleSpawn;
import elocindev.eldritch_end.entity.ominous_eye.OminousEyeEntity;
import elocindev.eldritch_end.entity.tentacle.TentacleEntity;
import elocindev.eldritch_end.registry.EntityRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ServerUtils {

    // Todo: Config; The maximum time window since last attack (in ticks)
    public static int EYE_THRESHOLD = 75;
    public static int HEALTH_DRAIN_THRESHOLD = 100;

    private static CETentacleSpawn TENTACLE_CFG = Configs.Mechanics.CORRUPTION.corruption_effects.tentacle_spawn;
    private static CEEyeSpawn OMINOUS_EYE_CFG = Configs.Mechanics.CORRUPTION.corruption_effects.ominous_eye_spawn;

    public static boolean hasValidTentacleConditions(ServerPlayerEntity serverPlayer) {        
        return CorruptionAPI.getTotalCorruptionLevel(serverPlayer) >= TENTACLE_CFG.getStartingLevel()
                && (serverPlayer.age - serverPlayer.getLastAttackTime() <= TENTACLE_CFG.getCombatDurationTicks())
                && serverPlayer.getRandom().nextFloat() <= TENTACLE_CFG.getSpawnChance();
    }

    public static boolean hasValidEyeConditions(ServerPlayerEntity serverPlayer) {
        return CorruptionAPI.getTotalCorruptionLevel(serverPlayer) >= OMINOUS_EYE_CFG.getStartingLevel()
                && (serverPlayer.age - serverPlayer.getLastAttackTime() <= OMINOUS_EYE_CFG.getCombatDurationTicks())
                && serverPlayer.getRandom().nextFloat() <= OMINOUS_EYE_CFG.getSpawnChance();
    }

    public static void summonTentacle(ServerPlayerEntity serverPlayer, World world) {
        TentacleEntity tentacle = EntityRegistry.TENTACLE.create(world);

        if (TENTACLE_CFG.getStartingLevel() == -1 || tentacle == null || serverPlayer == null || !serverPlayer.isOnGround()) return;

        tentacle.setPosition(serverPlayer.getPos());
        world.spawnEntity(tentacle);

        serverPlayer.setVelocity(0, TENTACLE_CFG.getLaunchVelocity(), 0);
        serverPlayer.velocityModified = true;
    }

    public static void summonOminousEye(ServerPlayerEntity serverPlayer, World world) {
        OminousEyeEntity ominousEye = EntityRegistry.OMINOUS_EYE.create(world);

        if (OMINOUS_EYE_CFG.getStartingLevel() == -1 || ominousEye == null || serverPlayer == null || !serverPlayer.isOnGround()) return;

        ominousEye.setPosition(serverPlayer.getPos());
        world.spawnEntity(ominousEye);
    }

    public static void healthDrainCheck(MinecraftServer server) {
        for (ServerPlayerEntity serverPlayer: server.getPlayerManager().getPlayerList()) {
            if (CorruptionAPI.getTotalCorruptionLevel(serverPlayer) >= HEALTH_DRAIN_THRESHOLD) {
                serverPlayer.damage(serverPlayer.getDamageSources().generic(), serverPlayer.getMaxHealth() * 0.1f);
            }
        }
    }

    public static void tentacleSummonCheck(MinecraftServer server) {
        for (ServerPlayerEntity serverPlayer: server.getPlayerManager().getPlayerList()) {
            if (ServerUtils.hasValidTentacleConditions(serverPlayer)) {
                ServerUtils.summonTentacle(serverPlayer, serverPlayer.getWorld());
            }
        }
    }

    public static void ominousEyeSummonCheck(MinecraftServer server) {
        for (ServerPlayerEntity serverPlayer: server.getPlayerManager().getPlayerList()) {
            if (ServerUtils.hasValidEyeConditions(serverPlayer)) {
                ServerUtils.summonOminousEye(serverPlayer, serverPlayer.getWorld());
            }
        }
    }
}
