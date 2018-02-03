/*
 * Copyright (C) 2016 Benoit CAYLA (benoit@famillecayla.fr)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joy.charts.gaugejs;
/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ChartCounterData {
    private Float value;
    private boolean hasData;
    private String label;
    private String code;
    private int maxValue;
    
    // Colors & thresolds
    private int thresoldBadToWarn;
    private int thresoldWarningToGood;
    private String colorBad;
    private String colorWarning;
    private String colorGood;
    // default values
    private static int DEFAULT_THRES_BAD_TO_WARN = 40;
    private static int DEFAULT_THRES_WARN_TO_GOOD = 60;
    private static String DEFAULT_BAD = "rgba(217,83,79,1)";
    private static String DEFAULT_WARN = "rgba(240,173,78,1)";
    private static String DEFAULT_GOOD = "rgba(92,184,92,1)";

    public ChartCounterData(Float _Value, String _Label, String _Code) {
        hasData = (_Value != null);
        value = (_Value == null ? 0 : _Value); 
        label = (_Label == null ? "" : _Label);
        code = _Code;
        maxValue = 100;
        thresoldBadToWarn = -1;
        thresoldWarningToGood = -1;
        colorBad = "";
        colorWarning = "";
        colorGood = "";
    }
    
    public void setThresolds(int BadToWarn, int WarnToGood) {
        thresoldBadToWarn = BadToWarn;
        thresoldWarningToGood = WarnToGood;
    }
    
    public void setColors(String bad, String warn, String good) {
        colorBad = (bad == null ? DEFAULT_BAD : bad);
        colorWarning = (warn == null ? DEFAULT_WARN : warn);
        colorGood = (good == null ? DEFAULT_GOOD : good);
    }
    
    public int getThresoldBadToWarn() {
        return (thresoldBadToWarn < 0 ? DEFAULT_THRES_BAD_TO_WARN : thresoldBadToWarn);
    }

    public int getThresoldwarningToGood() {
        return (thresoldWarningToGood < 0 ? DEFAULT_THRES_WARN_TO_GOOD : thresoldWarningToGood);
    }

    public String getColorBad() {
        return (colorBad.isEmpty() ? DEFAULT_BAD : colorBad);
    }

    public String getColorWarning() {
        return (colorWarning.isEmpty() ? DEFAULT_WARN : colorWarning);
    }

    public String getColorGood() {
        return (colorGood.isEmpty() ? DEFAULT_GOOD : colorGood);
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int MaxValue) {
        this.maxValue = MaxValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String Code) {
        this.code = Code;
    }
    
    public String getLabel()
    {
        return label;        
    }
    
    public boolean hasData() {
        return hasData;
    }
    
    public String getValue() {
        return (value == null? "No value": value.toString());
    }
    
    public String getBadValue() {
        return (value == null? String.valueOf(maxValue) : String.valueOf(maxValue - value));
    }

    public String getColor () {
        if (value > this.getThresoldwarningToGood()) 
            return this.getColorGood(); // "rgba(92,184,92,1)"; //"#5cb85c"; 
        else if (value > this.getThresoldBadToWarn()) 
            return this.getColorWarning(); // "rgba(240,173,78,1)"; //"#f0ad4e"; 
        else 
            return this.getColorBad(); // "rgba(217,83,92,79)"; //"#d9534f";
    }
}
