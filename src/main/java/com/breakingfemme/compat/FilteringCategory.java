package com.breakingfemme.compat;

import java.util.LinkedList;
import java.util.List;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModBlocks;

import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FilteringCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = new Identifier(BreakingFemme.MOD_ID, "textures/gui/filtering.png");
    public static final Identifier WARNING_TEXTURE = new Identifier(BreakingFemme.MOD_ID, "textures/gui/filtering_warning.png");
    public static final CategoryIdentifier<GrindingDisplay> FILTERING = CategoryIdentifier.of(BreakingFemme.MOD_ID, "filtering");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier()
    {
        return FILTERING;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("recipe.breakingfemme.filtering");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(ModBlocks.FUNNEL.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds)
    {
        //we add widgets here to a list that will be rendered
        final Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 43);
        List<Widget> widgets = new LinkedList<>(); //why a linked list??
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 98)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 36, startPoint.y + 13))
            .entries(display.getInputEntries().get(0)) //ingredient 0 is fluid input
        );
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 44, startPoint.y + 45))
            .entries(display.getInputEntries().get(1)) //ingredient 1 is filter
        );
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 104, startPoint.y + 73))
            .entries(display.getOutputEntries().get(0)) //output 0 is fluid output
        );
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 144, startPoint.y + 27))
            .entries(display.getOutputEntries().get(1)) //output 1 is item output
        );

        if(display instanceof FilteringDisplay fdisplay && fdisplay.harsh)
        {
            widgets.add( //i need to use another texture. cant just cut out a piece of the texture however i want. fuck you rei.
                Widgets.createTexturedWidget(WARNING_TEXTURE, new Rectangle(startPoint.x + 11, startPoint.y + 52, 39, 35))
            );
        }

        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 114; //height (98) + 16
    }
}
