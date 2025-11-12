package ewewukek.musketmod;

import net.minecraft.sounds.SoundEvent;

import static ewewukek.musketmod.MusketItem.damageMultiplierMax;
import static ewewukek.musketmod.MusketItem.damageMultiplierMin;

public class BlunderbussItem extends GunItem {
    public BlunderbussItem(Properties properties) {
        super(properties);
    }

    @Override
    public float bulletStdDev() {
        return Config.blunderbussBulletStdDev;
    }

    @Override
    public float bulletSpeed() {
        return Config.blunderbussBulletSpeed;
    }

    @Override
    public float damageMultiplierMin() {
        return damageMultiplierMin;
    }

    @Override
    public float damageMultiplierMax() {
        return damageMultiplierMax;
    }

    @Override
    public boolean twoHanded() {
        return true;
    }

    @Override
    public boolean ignoreInvulnerableTime() {
        return false;
    }


    @Override
    public SoundEvent fireSound() {
        return Sounds.MUSKET_FIRE;
    }

}
