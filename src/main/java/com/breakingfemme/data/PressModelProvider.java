package com.breakingfemme.data;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.mixin.VariantsBlockStateSupplierAccessor;
import com.breakingfemme.registries.press.PressHead;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class PressModelProvider implements DataProvider {
    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        this.registerVariants();
        return CompletableFuture.allOf(variantMap.entrySet().stream().map(entry -> {
            var head = entry.getKey();
            var headIsHereVariantId = entry.getValue().headModelId == null ? emptyId : entry.getValue().headModelId();
            var isDieVariantId = entry.getValue().dieModelId == null ? emptyId : entry.getValue().dieModelId();

            final BlockStateVariantMap.SingleProperty<Boolean> map = BlockStateVariantMap.create(PressHead.DIE);
            map.register(
                    false, List.of(BlockStateVariant.create().put(VariantSettings.MODEL, headIsHereVariantId))
            );

            map.register(
                    true, List.of(BlockStateVariant.create().put(VariantSettings.MODEL, isDieVariantId))
            );
            final VariantsBlockStateSupplier variantSupplier = PressModelVariantProvider.create().coordinate(map);
            var json = variantSupplier.get();

            var path = blockstatesPathResolver.resolveJson(head.getId());
            return DataProvider.writeToPath(writer, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    private final DataOutput.PathResolver blockstatesPathResolver;
    protected final Identifier emptyId = BreakingFemme.id("block/press/empty");
    private final IdentityHashMap<@NotNull PressHead, @NotNull Variant> variantMap = new IdentityHashMap<>();

    public record Variant(@Nullable Identifier headModelId, @Nullable Identifier dieModelId) {
        public Variant {
            assert !(headModelId() == null && dieModelId() == null);
        }
    }

    public PressModelProvider(DataOutput output) {
        this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates/press");
    }

    abstract public void registerVariants();

    public static final class PressModelVariantProvider extends VariantsBlockStateSupplier {

        private PressModelVariantProvider(Block block, List<BlockStateVariant> variants) {
            super(block, variants);
        }

        public static VariantsBlockStateSupplier create() {
            return new PressModelVariantProvider(null, ImmutableList.of(BlockStateVariant.create()));
        }

        @Override
        public PressModelVariantProvider coordinate(BlockStateVariantMap map) {

            ((VariantsBlockStateSupplierAccessor) (Object) this).breakingfemme$getVariantMaps().add(map);
            return this;
        }
    }

    public void registerHead(@NotNull PressHead head, @Nullable Identifier headModelId, @Nullable Identifier dieModelId) {
        this.variantMap.put(head, new Variant(headModelId, dieModelId));
    }


    @Override
    public String getName() {
        return "Press Model Definitions";
    }
}
