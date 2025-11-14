package ewewukek.musketmod;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

import static ewewukek.musketmod.MusketItem.damageMultiplierMax;
import static ewewukek.musketmod.MusketItem.damageMultiplierMin;

public class RifleItem extends GunItem {
    public RifleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public float bulletStdDev() {
        return MusketItem.bulletStdDev;
    }

    @Override
    public float bulletSpeed() {
        return MusketItem.bulletSpeed;
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

