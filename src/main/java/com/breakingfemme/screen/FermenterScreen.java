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

        renderProgress(context, x, y);
    }

    private void renderProgress(DrawContext ctx, int x, int y)
    {
        if(handler.isCooking())
        {
            //arrow top left at (85, 30); getScaledProgress goes from 0 to 1; arrow width,height: (22, 15)
            ctx.drawTexture(TEXTURE, x + 80, y + 35, 176, 0, (int)(handler.getScaledProgress() * 22) + 1, 15);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta)
    {
        renderBackground(ctx);
        super.render(ctx, mouseX, mouseY, delta);
        drawMouseoverTooltip(ctx, mouseX, mouseY); //TODO: drawing time left tooltip when hovering over arrow
    }
}
