package elocindev.eldritch_end;

import elocindev.eldritch_end.entity.ModEntities;
import elocindev.eldritch_end.entity.abberation.AberrationRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class EldritchEndClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ABERRATION, AberrationRenderer::new);
    }
}