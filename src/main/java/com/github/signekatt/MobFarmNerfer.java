package com.github.signekatt;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

import net.fabricmc.api.ModInitializer;

public class MobFarmNerfer implements ModInitializer {

    public static float FALL_DAMAGE_THRESHOLD = 0.5F;
    public static int CROWDING_THRESHOLD = 5;
    public static int CROWDING_RADIUS = 3;
    public static int MAX_PATH_CHECKING_DISTANCE = 25;

    @Override
    public void onInitialize() {
        File file = new File("config/mob_farm_nerfer.properties");
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream("config/mob_farm_nerfer.properties");
                Properties props = new Properties();
                props.load(inputStream);
                if (props.containsKey("fall_damage_threshold")) {
                    FALL_DAMAGE_THRESHOLD = Float.parseFloat(props.getProperty("fall_damage_threshold"));
                }

                if (props.containsKey("crowding_threshold")) {
                    CROWDING_THRESHOLD = Integer.parseInt(props.getProperty("crowding_threshold"));
                }

                if (props.containsKey("crowding_radius")) {
                    CROWDING_RADIUS = Integer.parseInt(props.getProperty("crowding_radius"));
                }

                if (props.containsKey("max_path_checking_distance")) {
                    MAX_PATH_CHECKING_DISTANCE = Integer.parseInt(props.getProperty("max_path_checking_distance"));
                }
                inputStream.close();
            } catch (IOException e) {
                // File should exist because we checked before
                e.printStackTrace();
            }
        }
    }
}