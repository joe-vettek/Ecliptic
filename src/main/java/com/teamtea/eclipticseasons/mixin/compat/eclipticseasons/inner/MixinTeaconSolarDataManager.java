package com.teamtea.eclipticseasons.mixin.compat.eclipticseasons.inner;


import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.misc.teacon.SolarTermCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SolarDataManager.class)
public class MixinTeaconSolarDataManager {

    @Shadow
    protected int solarTermsDay;

    @Inject(at = {@At("HEAD")}, method = {"setSolarTermsDay"}, cancellable = true)
    private void teacon$getSnowDepthAtBiome(int day, CallbackInfo ci) {
        this.solarTermsDay = SolarTermCalculator.getNowTerm().ordinal() * 7 + 0;
        ci.cancel();
    }

}