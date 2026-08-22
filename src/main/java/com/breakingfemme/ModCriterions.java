package com.breakingfemme;

import com.breakingfemme.criterions.WithinRangeOfFiveGTowerCriterion;
import net.minecraft.advancement.criterion.Criteria;

public class ModCriterions {
    public static final WithinRangeOfFiveGTowerCriterion WITHIN_RANGE_OF_FIVE_G_TOWER = Criteria.register(new WithinRangeOfFiveGTowerCriterion());

    public static void registerAll() {
    }
}
