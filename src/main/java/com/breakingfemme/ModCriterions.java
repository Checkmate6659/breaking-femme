package com.breakingfemme;

import com.breakingfemme.criterions.FormedValidMultiblockCriterion;
import com.breakingfemme.criterions.WithinRangeOfFiveGTowerCriterion;
import net.minecraft.advancement.criterion.Criteria;

public class ModCriterions {
    public static final WithinRangeOfFiveGTowerCriterion WITHIN_RANGE_OF_FIVE_G_TOWER = Criteria.register(new WithinRangeOfFiveGTowerCriterion());
    public static final FormedValidMultiblockCriterion FORMED_VALID_MULTIBLOCK = Criteria.register(new FormedValidMultiblockCriterion());
    public static void registerAll() {
    }
}
