package ewewukek.musketmod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(MusketMod.class);
    public static final Config INSTANCE = new Config();
    public static final int VERSION = 2;

    public double bulletMaxDistance;

    public double bulletStdDev;
    public double bulletSpeed;
    public double damageMin;
    public double damageMax;

    // 添加装填时间配置
    public int musketReloadDuration;
    public int pistolReloadDuration;
    public int blunderbussReloadDuration;
    public int rifleReloadDuration;
    public int musketWithBayonetReloadDuration;

    // 添加后坐力配置
    public double musketRecoilStrength;        // 火枪后坐力强度
    public double pistolRecoilStrength;        // 手枪后坐力强度
    public double blunderbussRecoilStrength;   // 霰弹枪后坐力强度
    public double rifleRecoilStrength;         // 步枪后坐力强度
    public double musketWithBayonetRecoilStrength; // 带刺刀火枪后坐力强度

    public double pistolBulletStdDev;
    public double pistolBulletSpeed;
    public double pistolDamageMin;
    public double pistolDamageMax;

    public static float blunderbussBulletStdDev;
    public static final float BLUNDERBUSS_BULLET_STD_DEV = 2.5f;
    public static float blunderbussBulletSpeed;
    public static final float BLUNDERBUSS_BULLET_SPEED = 160.0f;
    public static float blunderbussDamage;
    public static final float BLUNDERBUSS_DAMAGE = 25.0f;
    public static int blunderbussDurability;
    public static final int BLUNDERBUSS_DURABILITY = 200;

    public static void reload() {
        INSTANCE.setDefaults();
        INSTANCE.load();

        blunderbussBulletStdDev = BLUNDERBUSS_BULLET_STD_DEV;
        blunderbussBulletSpeed = BLUNDERBUSS_BULLET_SPEED;
        blunderbussDamage = BLUNDERBUSS_DAMAGE;
        blunderbussDurability = BLUNDERBUSS_DURABILITY;

        BulletEntity.maxDistance = INSTANCE.bulletMaxDistance;

        MusketItem.bulletStdDev = (float)Math.toRadians(INSTANCE.bulletStdDev);
        MusketItem.bulletSpeed = (float)(INSTANCE.bulletSpeed / 20);
        double maxEnergy = MusketItem.bulletSpeed * MusketItem.bulletSpeed;
        MusketItem.damageMultiplierMin = (float)(INSTANCE.damageMin / maxEnergy);
        MusketItem.damageMultiplierMax = (float)(INSTANCE.damageMax / maxEnergy);

        PistolItem.bulletStdDev = (float)Math.toRadians(INSTANCE.pistolBulletStdDev);
        PistolItem.bulletSpeed = (float)(INSTANCE.pistolBulletSpeed / 20);
        maxEnergy = PistolItem.bulletSpeed * PistolItem.bulletSpeed;
        PistolItem.damageMultiplierMin = (float)(INSTANCE.pistolDamageMin / maxEnergy);
        PistolItem.damageMultiplierMax = (float)(INSTANCE.pistolDamageMax / maxEnergy);

        // 添加装填时间配置应用到具体枪支类
        MusketItem.reloadDuration = INSTANCE.musketReloadDuration;
        PistolItem.reloadDuration = INSTANCE.pistolReloadDuration;

        logger.info("Configuration has been loaded");
    }

    private void setDefaults() {
        bulletMaxDistance = 256;

        bulletStdDev = 1;
        bulletSpeed = 180;
        damageMin = 20.5;
        damageMax = 21;

        // 设置默认装填时间（以tick为单位，20tick=1秒）
        musketReloadDuration = 30;
        pistolReloadDuration = 15;
        blunderbussReloadDuration = 20;
        rifleReloadDuration = 100;
        musketWithBayonetReloadDuration = 30;

        // 设置默认后坐力强度（数值越大，后坐力越强）
        musketRecoilStrength = 2.0;        // 中等后坐力
        pistolRecoilStrength = 1.5;        // 较小后坐力（单手武器）
        blunderbussRecoilStrength = 3.5;   // 强后坐力（大口径）
        rifleRecoilStrength = 2.5;         // 较强后坐力（长枪管）
        musketWithBayonetRecoilStrength = 2.2; // 稍弱后坐力（带刺刀增加重量）

        pistolBulletStdDev = 1.5;
        pistolBulletSpeed = 140;
        pistolDamageMin = 12;
        pistolDamageMax = 12.5;
    }

    private void load() {
        int version = 0;
        try (BufferedReader reader = Files.newBufferedReader(MusketMod.CONFIG_PATH)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int commentStart = line.indexOf('#');
                if (commentStart != -1) line = line.substring(0, commentStart);

                line.trim();
                if (line.length() == 0) continue;

                String errorPrefix = MusketMod.CONFIG_PATH+": line "+lineNumber+": ";
                try (Scanner s = new Scanner(line)) {
                    s.useLocale(Locale.US);
                    s.useDelimiter("\\s*=\\s*");

                    if (!s.hasNext()) {
                        logger.warn(errorPrefix+"parameter name is missing");
                        continue;
                    }
                    String key = s.next().trim();

                    if (!s.hasNextDouble()) {
                        logger.warn(errorPrefix+"value is missing/wrong/not a number");
                        continue;
                    }
                    double value = s.nextDouble();

                    switch (key) {
                    case "version":
                        version = (int)value;
                        break;
                    case "bulletMaxDistance":
                        bulletMaxDistance = value;
                        break;
                    case "bulletStdDev":
                        bulletStdDev = value;
                        break;
                    case "bulletSpeed":
                        bulletSpeed = value;
                        break;
                    case "damageMin":
                        damageMin = value;
                        break;
                    case "damageMax":
                        damageMax = value;
                        break;
                    case "pistolBulletStdDev":
                        pistolBulletStdDev = value;
                        break;
                    case "pistolBulletSpeed":
                        pistolBulletSpeed = value;
                        break;
                    case "pistolDamageMin":
                        pistolDamageMin = value;
                        break;
                    case "pistolDamageMax":
                        pistolDamageMax = value;
                        break;
                    // 添加装填时间配置读取
                    case "musketReloadDuration":
                        musketReloadDuration = (int)value;
                        break;
                    case "pistolReloadDuration":
                        pistolReloadDuration = (int)value;
                        break;
                    case "blunderbussReloadDuration":
                        blunderbussReloadDuration = (int)value;
                        break;
                    case "rifleReloadDuration":
                        rifleReloadDuration = (int)value;
                        break;
                    case "musketWithBayonetReloadDuration":
                        musketWithBayonetReloadDuration = (int)value;
                        break;
                    // 添加后坐力配置读取
                    case "musketRecoilStrength":
                        musketRecoilStrength = value;
                        break;
                    case "pistolRecoilStrength":
                        pistolRecoilStrength = value;
                        break;
                    case "blunderbussRecoilStrength":
                        blunderbussRecoilStrength = value;
                        break;
                    case "rifleRecoilStrength":
                        rifleRecoilStrength = value;
                        break;
                    case "musketWithBayonetRecoilStrength":
                        musketWithBayonetRecoilStrength = value;
                        break;
                    default:
                        logger.warn(errorPrefix+"unrecognized parameter name: "+key);
                    }
                }
            }
        } catch (NoSuchFileException e) {
            save();
            logger.info("Configuration file not found, default created");

        } catch (IOException e) {
            logger.warn("Could not read configuration file: ", e);
        }
        if (version < VERSION) {
            logger.info("Configuration file belongs to older version, updating");
            if (version < 2) {
                if (damageMax == 21.5) damageMax = 21;
            }
            save();
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(MusketMod.CONFIG_PATH)) {
            writer.write("version = "+VERSION+"\n");
            writer.write("\n");
            writer.write("# Maximum bullet travel distance (in blocks)\n");
            writer.write("bulletMaxDistance = "+bulletMaxDistance+"\n");
            writer.write("\n");
            writer.write("# Musket\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("bulletStdDev = "+bulletStdDev+"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("bulletSpeed = "+bulletSpeed+"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("damageMin = "+damageMin+"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("damageMax = "+damageMax+"\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("musketReloadDuration = "+musketReloadDuration+"\n");
            writer.write("\n");
            writer.write("# Pistol\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("pistolBulletStdDev = "+pistolBulletStdDev+"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("pistolBulletSpeed = "+pistolBulletSpeed+"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("pistolDamageMin = "+pistolDamageMin+"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("pistolDamageMax = "+pistolDamageMax+"\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("pistolReloadDuration = "+pistolReloadDuration+"\n");
            writer.write("\n");
            writer.write("# Blunderbuss\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("blunderbussReloadDuration = "+blunderbussReloadDuration+"\n");
            writer.write("\n");
            writer.write("# Rifle\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("rifleReloadDuration = "+rifleReloadDuration+"\n");
            writer.write("\n");
            writer.write("# Musket with Bayonet\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("musketWithBayonetReloadDuration = "+musketWithBayonetReloadDuration+"\n");
            writer.write("# Recoil strength (higher values = stronger recoil)\n");
            writer.write("musketRecoilStrength = "+musketRecoilStrength+"\n");
            writer.write("pistolRecoilStrength = "+pistolRecoilStrength+"\n");
            writer.write("blunderbussRecoilStrength = "+blunderbussRecoilStrength+"\n");
            writer.write("rifleRecoilStrength = "+rifleRecoilStrength+"\n");
            writer.write("musketWithBayonetRecoilStrength = "+musketWithBayonetRecoilStrength+"\n");
            writer.write("\n");
            writer.write("# Musket with Bayonet\n");
            writer.write("# Reload duration (in ticks, 20 ticks = 1 second)\n");
            writer.write("musketWithBayonetReloadDuration = "+musketWithBayonetReloadDuration+"\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }
}