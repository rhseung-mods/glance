package com.rhseung.glance.legacy_tooltip

import com.rhseung.glance.draw.DrawableTooltip
import com.rhseung.glance.legacy_tooltip.base.AbstractTooltip
import com.rhseung.glance.legacy_tooltip.factory.TooltipComponentFactoryManager
import com.rhseung.glance.legacy_tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.legacy_tooltip.util.TooltipBackground
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Util.toRangeSize
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.atan

class ArmorModelTooltip(val data: ArmorModelTooltipData) : AbstractTooltip<ArmorModelTooltip.ArmorModelTooltipData>(data, false) {
    class ArmorModelTooltipData(item: Item, stack: ItemStack, client: MinecraftClient) : AbstractTooltipData(item, stack, client) {
        val armorStand = ArmorStandEntity(EntityType.ARMOR_STAND, client.world);
        val slot = item.components[DataComponentTypes.EQUIPPABLE]!!.slot;

        val entityRenderDispatcher = client.entityRenderDispatcher;
        val mouseX = 45f;
        val mouseY = -45f;

        private val f = atan(mouseX / 40f);
        private val g = atan(mouseY / 40f);
        val quaternionf = Quaternionf().rotateZ(PI.toFloat());
        val quaternionf2 = Quaternionf().rotateX(g * 20f * PI.toFloat() / 180f);

        init {
            armorStand.equipStack(slot, stack);
            quaternionf.mul(quaternionf2);

            armorStand.bodyYaw = mouseX;
            armorStand.yaw = 180f + f * 40f;
            armorStand.pitch = -g * 20f;
            armorStand.headYaw = armorStand.yaw;
            armorStand.prevHeadYaw = armorStand.yaw;

            quaternionf2.conjugate();
            entityRenderDispatcher.rotation = quaternionf2;
        }

        override fun getTooltip(): DrawableTooltip {
            return DrawableTooltip();
        }
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return 0;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return 0;
    }

    override fun drawItems(
        textRenderer: TextRenderer,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        context: DrawContext
    ) {
        val x = x0 - 45.0;
        val y = y0.toDouble();

        val width = 30;
        val height = 60;

        // TODO: Background
        TooltipBackground.render(
            context,
            (x.toInt() + 5).toRangeSize(width), (y.toInt() - 28).toRangeSize(height),
            400,
            data.stack.rarity.formatting.toColor()
        )

        context.matrices.push();

        context.matrices.translate(x + 20, y + 33, 400.0);
        context.matrices.multiplyPositionMatrix(Matrix4f().scaling(30f, 30f, -30f));
        context.matrices.multiply(data.quaternionf);
        if (data.quaternionf2 != null) {
            data.quaternionf2.conjugate();
            data.entityRenderDispatcher.rotation = data.quaternionf2;
        }

        context.draw();
        DiffuseLighting.method_34742();

        data.entityRenderDispatcher.setRenderShadows(false);
        context.draw { vertexConsumers -> data.entityRenderDispatcher.render(data.armorStand, 0.0, 0.0, 0.0, 1.0f, context.matrices, vertexConsumers, 15728880) }
        data.entityRenderDispatcher.setRenderShadows(true);

        context.draw();
        DiffuseLighting.enableGuiDepthLighting();

        context.matrices.pop();
    }

    companion object {
        fun register() {
            TooltipComponentFactoryManager.set<ArmorModelTooltipData>(::ArmorModelTooltip);
            TooltipDataFactoryManager.set<ArmorItem>(::ArmorModelTooltipData);
        }
    }
}