package com.rhseung.glance.tooltip.util

import com.rhseung.glance.tooltip.base.AbstractTooltip
import com.rhseung.glance.tooltip.factory.TooltipDataFactoryManager
import com.rhseung.glance.tooltip.util.TooltipSeparator.*
import com.rhseung.glance.util.Color
import com.rhseung.glance.util.Color.Companion.toColor
import com.rhseung.glance.util.Util
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.gui.tooltip.TooltipPositioner
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipData
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import org.joml.Matrix4f
import org.joml.Quaternionf
import java.util.*
import kotlin.math.PI
import kotlin.math.atan

object TooltipUtil {
    fun Rarity.toTooltipSeparator(): TooltipSeparator {
        return when (this) {
            Rarity.COMMON -> COMMON;
            Rarity.UNCOMMON -> UNCOMMON;
            Rarity.RARE -> RARE;
            Rarity.EPIC -> EPIC;
            else -> throw IllegalArgumentException("Invalid rarity: $this");
        }
    }

    /**
     * [net.minecraft.item.Item.getTooltipData]
     */
    fun ItemStack.getTooltipDataWithClient(client: MinecraftClient): Optional<TooltipData> {
        val item = this.item;
        val original = item.getTooltipData(this);
        val compound = TooltipDataFactoryManager.find(item, this, client);
        compound.components = compound.components.filterNot { component ->
            component.getHeight(client.textRenderer) == 0 &&
            ((component as? AbstractTooltip<*>)?.canVanish ?: true)
        }.toMutableList();

        original.ifPresent { data -> compound.add(0, data) };

        return if (compound.size() > 0) Optional.of(compound) else original;
    }

    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        text: List<Text>,
        data: Optional<TooltipData>,
        x: Int,
        y: Int,
        stack: ItemStack
    ) {
        val list = text.map(Text::asOrderedText).map(TooltipComponent::of).toMutableList();
        data.ifPresent { datax: TooltipData -> list.add(if (list.isEmpty()) 0 else 1, TooltipComponent.of(datax)) };
        drawTooltip(context, textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, stack);
    }

    private fun usePadding(components: List<TooltipComponent>): Boolean {
        var isText = 0
        var isNotText = 0

        for (tooltip in components) {
            if (tooltip is OrderedTextTooltipComponent) isText++
            else isNotText++
        }

        return isText >= 2 && isNotText >= 1;
    }

    fun drawTooltip(
        context: DrawContext,
        textRenderer: TextRenderer,
        components: List<TooltipComponent>,
        x0: Int,
        y0: Int,
        positioner: TooltipPositioner,
        stack: ItemStack
    ) {
        if (components.isEmpty())
            return;

        val rarity = stack.rarity;
        val color = Color(stack.formattedName.style.color?.rgb ?: Color.WHITE.toInt());
        var tooltipWidth = 0;
        var tooltipHeight = if (components.size == 1) -2 else 0;

        for (r in components.indices) {
            val component = components[r];
            val k = component.getWidth(textRenderer);   // TODO: Icon Display

            if (k > tooltipWidth)
                tooltipWidth = k;
            tooltipHeight += component.getHeight(textRenderer);
        }

        val vector2ic = positioner.getPosition(context.scaledWindowWidth, context.scaledWindowHeight, x0, y0, tooltipWidth, tooltipHeight);
        val x0Hover = vector2ic.x();
        val y0Hover = vector2ic.y();

        context.matrices.push();
        val z = 400;

        TooltipBackground.render(
            context,
            x0Hover,
            y0Hover,
            tooltipWidth,
            tooltipHeight
                + if (components.size <= 1) 0 else TooltipConstants.SEPARATOR_MARGIN
                + if (usePadding(components)) TooltipConstants.BETWEEN_TOOLTIP_TEXT else 0,
            z,
            rarity.formatting.toColor()
        );

//        val item = stack.item;
//        if (item is ArmorItem) {
//            val entity = ArmorStandEntity(EntityType.ARMOR_STAND, MinecraftClient.getInstance().world);
//            val slot = item.components[DataComponentTypes.EQUIPPABLE]!!.slot;
//            entity.equipStack(slot, stack);
//
//            val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher;
//            val mouseX = 45f;
//            val mouseY = -45f;
//            val f = atan(mouseX / 40f);
//            val g = atan(mouseY / 40f);
//            val quaternionf = Quaternionf().rotateZ(PI.toFloat());
//            val quaternionf2 = Quaternionf().rotateX(g * 20.0f * PI.toFloat() / 180F);
//            quaternionf.mul(quaternionf2);
//
//            entity.bodyYaw = mouseX;
//            entity.yaw = 180f + f * 40f;
//            entity.pitch = -g * 20f;
//            entity.headYaw = entity.yaw;
//            entity.prevHeadYaw = entity.yaw;
//
//            context.matrices.push();
//            context.matrices.translate(x0Hover - 30.0, y0Hover.toDouble(), z.toDouble());
//            context.matrices.multiplyPositionMatrix(Matrix4f().scaling(30f, 30f, -30f));
//            context.matrices.multiply(quaternionf);
//            if (quaternionf2 != null) {
//                quaternionf2.conjugate();
//                entityRenderDispatcher.rotation = quaternionf2;
//            }
//
//            context.draw();
//            DiffuseLighting.method_34742();
//            entityRenderDispatcher.setRenderShadows(false);
//            context.draw { vertexConsumers -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 1.0f, context.matrices, vertexConsumers, 15728880) };
//            context.draw();
//            entityRenderDispatcher.setRenderShadows(true);
//            context.matrices.pop();
//            DiffuseLighting.enableGuiDepthLighting();
//        }
//
        context.matrices.translate(0.0f, 0.0f, z.toFloat());
        var y = y0Hover;

        for (r in components.indices) {
            val component = components[r];

            if (r == 1)
                y += TooltipConstants.SEPARATOR_MARGIN + if (usePadding(components)) TooltipConstants.BETWEEN_TOOLTIP_TEXT else 0;

            component.drawText(
                textRenderer,
                x0Hover,
                y,
                context.matrices.peek().positionMatrix,
                Util.get(context, "vertexConsumers") as Immediate
            )
            y += component.getHeight(textRenderer) + (if (r == 0) 2 else 0)
        }

        y = y0Hover;

        for (r in components.indices) {
            val component = components[r];

            component.drawItems(
                textRenderer,
                x0Hover,
                y,
                tooltipWidth,
                tooltipHeight + TooltipConstants.SEPARATOR_MARGIN,
                context
            )

            if (components.size > 1 && r == 0) {
                rarity.toTooltipSeparator().draw(
                    context,
                    x0Hover,
                    x0Hover + tooltipWidth,
                    y + component.getHeight(textRenderer) + TooltipConstants.SEPARATOR_MARGIN / 2
                );
                y += TooltipConstants.SEPARATOR_MARGIN;
            }

            y += component.getHeight(textRenderer) + (if (r == 0) 2 else 0);
        }

        context.matrices.pop();
    }
}