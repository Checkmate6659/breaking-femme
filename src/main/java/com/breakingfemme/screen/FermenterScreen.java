package com.breakingfemme.screen;

import com.breakingfemme.BreakingFemme;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FermenterScreen extends HandledScreen<FermenterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(BreakingFemme.MOD_ID, "textures/gui/fermenter.png");

    public FermenterScreen(FermenterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundHeight = 174; //this screen is slightly bigger
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init()
    {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        //default titleY is 6
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, 176, backgroundHeight);

        renderCapacity(context, x, y);
        renderProgress(context, x, y);
        renderTemperature(context, x, y);
    }

    //draw the little barrels showing multiblock capacity
    private void renderCapacity(DrawContext ctx, int x, int y)
    {
        int cap = handler.nBarrels();
        for(int i = 0; i < 4; i++)
        {
            if (i < cap)
                //draw the barrel the right number of times, from bottom to top
                ctx.drawTexture(TEXTURE, x + 79, y + 69 - i * 18, 211, 0, 11, 16);
            else
                //draw lock next to inaccessible output slots
                ctx.drawTexture(TEXTURE, x + 128, y + 70 - i * 18, 239, 1, 15, 15);
        }
    }

    //draw the thermometer
    private void renderTemperature(DrawContext ctx, int x, int y)
    {
        int height = handler.getThermometerHeight();
        //draw the bottom part of the red overlay
        ctx.drawTexture(TEXTURE, x + 8, y + height - 10, 240, height, 16, 71); //71: max thermometer height (excluding tip); is not drawing a bunch of transparent pixels worth it?

        //draw the top part (to get rounded top)
        ctx.drawTexture(TEXTURE, x + 14, y + height - 11, 246, height - 1, 4, 1); //this allows blue to red gradient on thermometer
    }

    //draw the bubbles with the right colors, white for stage in progress, or brown in case of risk of failure (wrong params at the moment)
    private void renderProgress(DrawContext ctx, int x, int y)
    {
        if(!handler.isProcessing()) return;

        for(int i = 0; i < 5; i++) //there are 5 drawable bubbles for the 5 stages
        {
            int color = handler.getBubbleColor(i); //24 will mean a transparent part of the image get picked and not drawn
            ctx.drawTexture(TEXTURE, x + 96 + i * 4, y + 45, 176 + i * 7, color * 10, 7, 10); //staggering the bubble overlays allows easier rendering this way
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta)
    {
        renderBackground(ctx);
        super.render(ctx, mouseX, mouseY, delta);
        drawMouseoverTooltip(ctx, mouseX, mouseY); //TODO: drawing "time left" tooltip when hovering over arrow
    }
}
