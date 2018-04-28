package com.superfamous.superphotopicker.photofilter.renderer;

import android.media.effect.EffectFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josongmin on 2016-04-07.
 */
public class Filter {

    private boolean isOriginal = false;
    private List<FilterModel> listFilters = null;
    private String filterName;
    private Object tag;

    public Filter(){
        ;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setListFilters(List<FilterModel> listFilters) {
        this.listFilters = listFilters;
    }

    public List<FilterModel> getListFilters() {
        return listFilters;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }


    /**Factory*/
    public static final Builder Builder(){
        return new Builder();
    }

    /**빌더 클래스*/
    public static class Builder{

        List<FilterModel> listFilters = null;
        Filter filter = null;

        public Builder() {
            filter =  new Filter();
            listFilters = new ArrayList<FilterModel>();
        }

        public Filter build(){
            filter.setListFilters(listFilters);
            return filter;
        }

        public Builder setFilterName(String filterName){
            filter.setFilterName(filterName);
            return this;
        }

        public Builder setTag(Object tag){
            filter.setTag(tag);
            return this;
        }

        public Builder setOriginal(boolean original){
            filter.setOriginal(original);
            return this;
        }

        /**0 ~ 1*/
        public Builder addEffectAutoFix(float scale){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_AUTOFIX, "scale", scale));
            return this;
        }

        public Builder addEffectBlackwhite(float black, float white){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_BLACKWHITE, "black", black, "white", white));
            return this;
        }

        public Builder addEffectBrightness(float brightness){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_BRIGHTNESS, "brightness", brightness));
            return this;
        }

        public Builder addEffectContrast(float contrast){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_CONTRAST, "contrast", contrast));
            return this;
        }

        public Builder addEffectCrossProcess(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_CROSSPROCESS));
            return this;
        }

        public Builder addEffectDocumentary(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_DOCUMENTARY));
            return this;
        }

        public Builder addEffectDuotone(int firstColor, int secondColor){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_DUOTONE, "first_color", firstColor, "secondColor", secondColor));
            return this;
        }

        //0~1
        public Builder addEffectFilllight(float strength){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_FILLLIGHT, "strength", strength));
            return this;
        }

        /***/
        public Builder addEffectGrain(float strength){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_GRAIN, "strength", strength));
            return this;
        }

        public Builder addEffectGrayScale(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_GRAYSCALE));
            return this;
        }

        public Builder addEffectLomoish(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_LOMOISH));
            return this;
        }

        public Builder addEffectPosterize(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_POSTERIZE));
            return this;
        }


        /** -1 ~ 0(no change)*/
        public Builder addEffectSaturate(float scale){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_SATURATE, "scale", scale));
            return this;
        }

        public Builder addEffectSepia(){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_SEPIA));
            return this;
        }

        /** 0(no change) ~ 1.0*/
        public Builder addEffectSharpen(float scale){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_SHARPEN, "scale", scale));
            return this;
        }

        /**0(cool) ~ 0.5(no change) ~ 1(warm)*/
        public Builder addEffectTemperature(float scale){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_TEMPERATURE, "scale", scale));
            return this;
        }

        /** argb color*/
        public Builder addEffectTint(float tint){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_TINT, "tint", tint));
            return this;
        }

        /**가운데만 집중되는 필터.0(no change)~1 */
        public Builder addEffectVignette(float scale){
            listFilters.add(FilterModel.build(EffectFactory.EFFECT_VIGNETTE, "scale", scale));
            return this;
        }

    }

    public static class FilterModel{
        public String effectType;
        public List<Object> listParmas = new ArrayList<>();

        public FilterModel(String effectType, Object... params) {
            this.effectType = effectType;
            for(Object o : params){
                listParmas.add(o);
            }
        }

        public static final FilterModel build(String effectType, Object... params){
            return new FilterModel(effectType, params);
        }

    }
}
