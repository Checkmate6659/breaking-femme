package com.breakingfemme.criterions;

import com.breakingfemme.BreakingFemme;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Objects;

public class FormedValidMultiblockCriterion extends AbstractCriterion<FormedValidMultiblockCriterion.Conditions> {
    static final Identifier ID = Identifier.of(BreakingFemme.MOD_ID, "formed_valid_multiblock");

    @Override
    protected FormedValidMultiblockCriterion.Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        var typeId = new Identifier(JsonHelper.getString(obj, "type"));
        var type = Registries.BLOCK_ENTITY_TYPE.getOrEmpty(typeId).orElseThrow(() -> new JsonSyntaxException("Unknown block entity type '" + typeId + "'."));
        return new FormedValidMultiblockCriterion.Conditions(playerPredicate, type);
    }


    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, BlockEntityType<?> type) {
        var condition = new Conditions(LootContextPredicate.EMPTY, type);
        this.trigger(player, it -> it.matches(condition));
    }

    public static class Conditions extends AbstractCriterionConditions {
        public BlockEntityType<?> type;

        public Conditions(LootContextPredicate player, BlockEntityType<?> type) {
            super(ID, player);
            assert type != null;
            this.type = type;
        }

        public boolean matches(Conditions other) {
            return type.equals(other.type);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            var json = super.toJson(predicateSerializer);
            json.addProperty("type", Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getId(type)).toString());
            return json;
        }
    }
}
