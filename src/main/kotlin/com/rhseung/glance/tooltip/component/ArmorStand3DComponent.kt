package com.rhseung.glance.tooltip.component

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.ItemStack
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.atan

class ArmorStand3DComponent(
    val itemStack: ItemStack
) : GlanceTooltipComponent {

    companion object {
        var rotationTimer = 0f;

        fun updateTimer() {
            rotationTimer += 0.3f;
            if (rotationTimer >= 180f)
                rotationTimer = -180f;
        }
    }

    override fun getWidth(textRenderer: TextRenderer): Int {
        return 16 + 2;
    }

    override fun getHeight(textRenderer: TextRenderer): Int {
        return 16 + 2;
    }

    override fun draw(
        context: DrawContext,
        textRenderer: TextRenderer,
        innerWidth: Int,
        innerHeight: Int,
        x0: Int,
        y0: Int,
        outerX: Int,
        outerY: Int
    ) {
        val innerx = x0 + 1;
        val innery = y0 + 1;

        // item
        val client = MinecraftClient.getInstance();
        val armorStand = ArmorStandEntity(EntityType.ARMOR_STAND, client.world);
        val slot = itemStack.components[DataComponentTypes.EQUIPPABLE]!!.slot;

        val entityRenderDispatcher = client.entityRenderDispatcher;
        val mouseX = rotationTimer;
        val mouseY = -30f;

        val f = atan(mouseX / 40f);
        val g = atan(mouseY / 40f);
        val quaternionf = Quaternionf().rotateZ(PI.toFloat());
        val quaternionf2 = Quaternionf().rotateX(g * 20f * PI.toFloat() / 180f);

        armorStand.equipStack(slot, itemStack);
        armorStand.isInvisible = true;
        quaternionf.mul(quaternionf2);

        armorStand.bodyYaw = mouseX;
        armorStand.yaw = 180f + f * 40f;
        armorStand.pitch = -g * 20f;
        armorStand.headYaw = armorStand.yaw;
        armorStand.prevHeadYaw = armorStand.yaw;

        quaternionf2.conjugate();
        entityRenderDispatcher.rotation = quaternionf2;

        val deltaY = when (slot) {
            EquipmentSlot.HEAD -> 33
            EquipmentSlot.CHEST -> 21
            EquipmentSlot.LEGS -> 15
            EquipmentSlot.FEET -> 10
            else -> 21
        };

        var scale = when (slot) {
            EquipmentSlot.HEAD -> 16f
            EquipmentSlot.CHEST -> 12f
            EquipmentSlot.LEGS -> 14f
            EquipmentSlot.FEET -> 18f
            else -> 17f
        };

        context.matrices.push();
        context.matrices.translate(innerx.toFloat() + 7, innery.toFloat() + deltaY, 400f);
        context.matrices.multiplyPositionMatrix(Matrix4f().scaling(scale, scale, -scale));
        context.matrices.multiply(quaternionf);

        quaternionf2.conjugate();
        entityRenderDispatcher.rotation = quaternionf2;

        context.draw();
        DiffuseLighting.method_34742();
        entityRenderDispatcher.setRenderShadows(false);

        context.draw { vertexConsumers -> entityRenderDispatcher.render(armorStand, 0.0, 0.0, 0.0, 1f, context.matrices, vertexConsumers, 15728880) };
        entityRenderDispatcher.setRenderShadows(true);

        context.draw();
        DiffuseLighting.enableGuiDepthLighting();

        context.matrices.pop();
    }
}