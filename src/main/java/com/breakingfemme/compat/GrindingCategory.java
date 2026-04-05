package com.breakingfemme.compat;

import java.util.LinkedList;
import java.util.List;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.item.ModItems;

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
public class GrindingCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = new Identifier(BreakingFemme.MOD_ID, "textures/gui/grinding.png"); //TODO: animated texture w/ pestle moving around
    public static final CategoryIdentifier<GrindingDisplay> GRINDING = CategoryIdentifier.of(BreakingFemme.MOD_ID, "grinding");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier()
    {
        return GRINDING;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("recipe.breakingfemme.grinding");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(ModItems.MORTAR_PESTLE.getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds)
    {
        //we add widgets here to a list that will be rendered
        final Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 35);
        List<Widget> widgets = new LinkedList<>(); //why a linked list??
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 82)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 23, startPoint.y + 33))
            .entries(display.getInputEntries().get(0)) //ingredient 0 is input
        );
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 137, startPoint.y + 33))
            .entries(display.getOutputEntries().get(0)) //output 0 is... output. well yeah.
        );

        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 90; //TODO: adjust!
    }
}
