package ewewukek.musketmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ClientUtilities {
	public static Optional<HumanoidModel.ArmPose> getArmPose(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.swinging && !stack.isEmpty() && stack.getItem() instanceof GunItem) {
			GunItem gunItem = (GunItem) stack.getItem();
			if (gunItem.canUseFrom(player, hand) && GunItem.isLoaded(stack)) {
				return Optional.of(HumanoidModel.ArmPose.CROSSBOW_HOLD);
			}
		}
		return Optional.empty();
	}

	public static boolean disableMainHandEquipAnimation;
	public static boolean disableOffhandEquipAnimation;

	public static void renderGunInHand(ItemInHandRenderer renderer, AbstractClientPlayer player, InteractionHand hand, float partialTicks, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack, PoseStack matrixStack, MultiBufferSource render, int packedLight) {
		HumanoidArm handside = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		boolean isRightHand = handside == HumanoidArm.RIGHT;
		float sign = isRightHand ? 1 : -1;

		GunItem gunItem = (GunItem) stack.getItem();
		if (!gunItem.canUseFrom(player, hand)) {
			matrixStack.pushPose();
			matrixStack.translate(sign * 0.5, -0.5 - 0.6 * equipProgress, -0.7);
			matrixStack.mulPose(Axis.XP.rotationDegrees(70));
			renderer.renderItem(player, stack, isRightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !isRightHand, matrixStack, render, packedLight);
			matrixStack.popPose();
			return;
		}

		// 添加射击后坐力效果
		if (stack == GunItem.getActiveStack(hand)) {
			setEquipAnimationDisabled(hand, true);
			
			// 计算后坐力强度
			double recoilStrength = getRecoilStrength(stack);
			
			// 应用视觉后坐力（快速向上移动然后缓慢恢复）
			float recoilProgress = Math.min(1.0f, (player.getUseItemRemainingTicks() - partialTicks) / 10.0f);
			if (recoilProgress > 0) {
				float recoilOffset = (float)(recoilStrength * 0.02 * (1 - recoilProgress));
				matrixStack.translate(0, -recoilOffset, 0);
			}
		}

		matrixStack.pushPose();
		matrixStack.translate(sign * 0.15, -0.25, -0.35);

		if (swingProgress > 0) {
			float swingSharp = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
			float swingNormal = Mth.sin(swingProgress * (float) Math.PI);

			if (gunItem == Items.MUSKET_WITH_BAYONET) {
				matrixStack.translate(sign * -0.05 * swingNormal, 0, 0.05 - 0.3 * swingSharp);
				matrixStack.mulPose(Axis.YP.rotationDegrees(5 * swingSharp));
			} else {
				matrixStack.translate(sign * 0.05 * (1 - swingNormal), 0.05 * (1 - swingNormal), 0.05 - 0.4 * swingSharp);
				matrixStack.mulPose(Axis.XP.rotationDegrees(180 + sign * 20 * (1 - swingSharp)));
			}

		} else if (player.isUsingItem() && player.getUsedItemHand() == hand) {
			float usingDuration = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTicks + 1);
            gunItem = (GunItem) stack.getItem();
			int reloadDuration = gunItem.getReloadDuration();
			
			if (usingDuration > 0 && usingDuration < reloadDuration) {
				matrixStack.translate(0, -0.3, 0.05);
				matrixStack.mulPose(Axis.XP.rotationDegrees(60));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(10));

				// 基于总装填时间的比例计算装填阶段时间
				int loadingStage1 = reloadDuration / 6;      // 总装填时间的1/6
				int loadingStage2 = reloadDuration / 3;     // 总装填时间的1/3
				int loadingStage3 = reloadDuration * 2 / 3;  // 总装填时间的2/3
				
				// 减缓装填动画速度：延长动画持续时间，减小移动幅度
				if ((usingDuration >= loadingStage1 && usingDuration <= loadingStage1 + 10) || 
				    (usingDuration >= loadingStage2 && usingDuration <= loadingStage2 + 10) || 
				    (usingDuration >= loadingStage3 && usingDuration <= loadingStage3 + 10)) {
				    float t;
				    if (usingDuration < loadingStage1 + 4) {
				        t = (usingDuration - loadingStage1) / 4;  // 延长动画持续时间
				        t = Mth.sin((float) Math.PI / 2 * Mth.sqrt(t));
				    } else if (usingDuration < loadingStage2 + 4) {
				        t = (usingDuration - loadingStage2) / 4;  // 延长动画持续时间
				        t = Mth.sin((float) Math.PI / 2 * Mth.sqrt(t));
				    } else {
				        t = (usingDuration - loadingStage3) / 4;  // 延长动画持续时间
				        t = Mth.sin((float) Math.PI / 2 * Mth.sqrt(t));
				    }
				    matrixStack.translate(0, 0, 0.015 * t);  // 减小移动幅度
				}
				if (gunItem == Items.PISTOL) {
				    matrixStack.translate(0, 0, -0.12);
				}
			}
		}

		if (isEquipAnimationDisabled(hand)) {
			if (equipProgress == 0) {
				setEquipAnimationDisabled(hand, false);
			}
		} else {
			matrixStack.translate(0, -0.6 * equipProgress, 0);
		}
		renderer.renderItem(player, stack, isRightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !isRightHand, matrixStack, render, packedLight);
		matrixStack.popPose();
	}

	public static boolean isEquipAnimationDisabled(InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND) {
			return disableMainHandEquipAnimation;
		} else {
			return disableOffhandEquipAnimation;
		}
	}

	public static void setEquipAnimationDisabled(InteractionHand hand, boolean disabled) {
		if (hand == InteractionHand.MAIN_HAND) {
			disableMainHandEquipAnimation = disabled;
		} else {
			disableOffhandEquipAnimation = disabled;
		}
	}

	// 获取当前枪支的后坐力强度（客户端版本）
	private static double getRecoilStrength(ItemStack stack) {
		if (stack.getItem() == Items.MUSKET) {
			return Config.INSTANCE.musketRecoilStrength;
		} else if (stack.getItem() == Items.PISTOL) {
			return Config.INSTANCE.pistolRecoilStrength;
		} else if (stack.getItem() == Items.BLUNDERBUSS) {
			return Config.INSTANCE.blunderbussRecoilStrength;
		} else if (stack.getItem() == Items.RIFLE) {
			return Config.INSTANCE.rifleRecoilStrength;
		} else if (stack.getItem() == Items.MUSKET_WITH_BAYONET) {
			return Config.INSTANCE.musketWithBayonetRecoilStrength;
		}
		return 1.0; // 默认值
	}

}