package elocindev.eldritch_end.datagen;

import com.google.gson.JsonElement;
import elocindev.eldritch_end.registry.BlockRegistry;
import elocindev.eldritch_end.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(BlockRegistry.PRIMORDIAL_PLANKS);
        generator.registerDoor(BlockRegistry.PRIMORDIAL_DOOR);
        generator.registerTrapdoor(BlockRegistry.PRIMORDIAL_TRAPDOOR);
        generator.registerTintableCross(BlockRegistry.ABYSMAL_ROOTS, BlockStateModelGenerator.TintType.NOT_TINTED);

        registerSlab(BlockRegistry.PRIMORDIAL_SLAB, BlockRegistry.PRIMORDIAL_PLANKS, generator.modelCollector, generator.blockStateCollector);
        registerStairs(BlockRegistry.PRIMORDIAL_STAIRS, BlockRegistry.PRIMORDIAL_PLANKS, generator.modelCollector, generator.blockStateCollector);
        registerButton(BlockRegistry.PRIMORDIAL_BUTTON, BlockRegistry.PRIMORDIAL_PLANKS, generator.modelCollector, generator.blockStateCollector);
        registerFence(BlockRegistry.PRIMORDIAL_FENCE, BlockRegistry.PRIMORDIAL_PLANKS, generator.modelCollector, generator.blockStateCollector);
        registerFenceGate(BlockRegistry.PRIMORDIAL_FENCE_GATE, BlockRegistry.PRIMORDIAL_PLANKS, generator.modelCollector, generator.blockStateCollector);

        generator.registerSimpleCubeAll(BlockRegistry.ETYR_ORE);
        generator.registerSimpleCubeAll(BlockRegistry.ETYR_BLOCK);
        generator.registerSimpleCubeAll(BlockRegistry.ETYR_TILES);
        generator.registerDoor(BlockRegistry.ETYR_DOOR);
        generator.registerTrapdoor(BlockRegistry.ETYR_TRAPDOOR);
        registerBars(BlockRegistry.ETYR_BARS, generator.modelCollector, generator.blockStateCollector);
        generator.registerAxisRotated(BlockRegistry.ETYR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(ItemRegistry.SILVER_KEY, Models.GENERATED);
        generator.register(ItemRegistry.NECRONOMICON, Models.GENERATED);
        generator.register(ItemRegistry.RAW_ETYR, Models.GENERATED);
        generator.register(ItemRegistry.ETYR_INGOT, Models.GENERATED);
    }

    private void registerBars(Block barBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        Identifier identifier = ModelIds.getBlockSubModelId(barBlock, "_post_ends");
        Identifier identifier2 = ModelIds.getBlockSubModelId(barBlock, "_post");
        Identifier identifier3 = ModelIds.getBlockSubModelId(barBlock, "_cap");
        Identifier identifier4 = ModelIds.getBlockSubModelId(barBlock, "_cap_alt");
        Identifier identifier5 = ModelIds.getBlockSubModelId(barBlock, "_side");
        Identifier identifier6 = ModelIds.getBlockSubModelId(barBlock, "_side_alt");
        blockStateCollector.accept(MultipartBlockStateSupplier.create(barBlock).with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).with(When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)).with(When.create().set(Properties.NORTH, true).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)).with(When.create().set(Properties.NORTH, false).set(Properties.EAST, true).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, true).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)).with(When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5)).with(When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier6)).with(When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier6).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
        registerItemModel(barBlock);
    }

    public final void registerItemModel(Block block, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(block), modelCollector);
        }
    }

    private void registerFence(Block fenceBlock, Block plankBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = TextureMap.all(plankBlock);
        Identifier identifier = Models.FENCE_POST.upload(fenceBlock, textureMap, modelCollector);
        Identifier identifier2 = Models.FENCE_SIDE.upload(fenceBlock, textureMap, modelCollector);
        blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, identifier, identifier2));
        Identifier identifier3 = Models.FENCE_INVENTORY.upload(fenceBlock, textureMap, modelCollector);
        registerParentedItemModel(fenceBlock, identifier3, modelCollector);
    }

    private void registerFenceGate(Block fenceGateBlock, Block plankBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = TextureMap.all(plankBlock);
        Identifier identifier = Models.TEMPLATE_FENCE_GATE_OPEN.upload(fenceGateBlock, textureMap, modelCollector);
        Identifier identifier2 = Models.TEMPLATE_FENCE_GATE.upload(fenceGateBlock, textureMap,modelCollector);
        Identifier identifier3 = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(fenceGateBlock, textureMap, modelCollector);
        Identifier identifier4 = Models.TEMPLATE_FENCE_GATE_WALL.upload(fenceGateBlock, textureMap, modelCollector);
        blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, identifier, identifier2, identifier3, identifier4));

        Identifier identifier5 = Models.FENCE_INVENTORY.upload(fenceGateBlock, textureMap, modelCollector);
        registerParentedItemModel(fenceGateBlock, identifier5, modelCollector);
    }

    /** Takes in the slab block as well as a plank block which acts as the texture. */
    private void registerSlab(Block slabBlock, Block plankBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = TextureMap.all(plankBlock);
        TextureMap textureMap2 = TextureMap.sideEnd(TextureMap.getSubId(plankBlock, ""), textureMap.getTexture(TextureKey.TOP));

        Identifier identifier = Models.SLAB.upload(slabBlock, textureMap2, modelCollector);
        Identifier identifier2 = Models.SLAB_TOP.upload(slabBlock, textureMap2, modelCollector);
        Identifier identifier3 = Models.CUBE_COLUMN.uploadWithoutVariant(slabBlock, "_double", textureMap2, modelCollector);

        blockStateCollector.accept(createSlabBlockState(slabBlock, identifier, identifier2, identifier3));
    }

    private void registerButton(Block buttonBlock, Block plankBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = TextureMap.all(plankBlock);

        Identifier identifier = Models.BUTTON.upload(buttonBlock, textureMap, modelCollector);
        Identifier identifier2 = Models.BUTTON_PRESSED.upload(buttonBlock, textureMap, modelCollector);
        Identifier identifier3 = Models.BUTTON_INVENTORY.upload(buttonBlock, textureMap, modelCollector);

        blockStateCollector.accept(createButtonBlockState(buttonBlock, identifier, identifier2));
        registerParentedItemModel(buttonBlock, identifier3, modelCollector);
    }

    private void registerStairs(Block stairBlock, Block plankBlock, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = TextureMap.all(plankBlock);
        TextureMap textureMap2 = TextureMap.sideEnd(TextureMap.getSubId(plankBlock, ""), textureMap.getTexture(TextureKey.TOP));

        Identifier identifier = Models.STAIRS.upload(stairBlock, textureMap2, modelCollector);

        blockStateCollector.accept(createStairsBlockState(stairBlock, identifier, identifier, identifier));
    }

    public final void registerFronds(Block block, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<BlockStateSupplier> blockStateCollector) {
        TextureMap textureMap = (new TextureMap()).put(TextureKey.BOTTOM, TextureMap.getId(Blocks.END_STONE)).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.SIDE, TextureMap.getSubId(block, ""));
        blockStateCollector.accept(createSingletonBlockState(block, Models.CUBE_COLUMN_HORIZONTAL.upload(block, textureMap, modelCollector)));
    }

    public static VariantsBlockStateSupplier createSingletonBlockState(Block block, Identifier modelId) {
        return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId));
    }

    public static BlockStateSupplier createSlabBlockState(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId) {
        return VariantsBlockStateSupplier.create(slabBlock).coordinate(BlockStateVariantMap.create(Properties.SLAB_TYPE).register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId)));
    }

    public static BlockStateSupplier createStairsBlockState(Block stairsBlock, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId) {
        return VariantsBlockStateSupplier.create(stairsBlock).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)));
    }

    public static BlockStateSupplier createButtonBlockState(Block buttonBlock, Identifier regularModelId, Identifier pressedModelId) {
        return VariantsBlockStateSupplier.create(buttonBlock).coordinate(BlockStateVariantMap.create(Properties.POWERED).register(false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId)).register(true, BlockStateVariant.create().put(VariantSettings.MODEL, pressedModelId))).coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING).register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create()).register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R180)));
    }

    public void registerParentedItemModel(Block block, Identifier parentModelId, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new SimpleModelSupplier(parentModelId));
    }
}
