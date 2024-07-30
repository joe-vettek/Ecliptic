package com.teamtea.ecliptic.common.handler;

import com.teamtea.ecliptic.api.constant.solar.Season;
import com.teamtea.ecliptic.api.constant.solar.SolarTerm;
import com.teamtea.ecliptic.common.AllListener;
import com.teamtea.ecliptic.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;


public class SolarUtil {


    public static SolarDataManager getProvider(Level level) {
        return AllListener.getSaveData(level);
    }

    public static Season getSeason(Level level) {
        return AllListener.getSaveDataLazy(level).map(data -> data.getSolarTerm().getSeason()).orElse(Season.NONE);
    }

    public static SolarTerm getSolarTerm(Level level) {
        return AllListener.getSaveDataLazy(level).map(SolarDataManager::getSolarTerm).orElse(SolarTerm.NONE);
    }
}
