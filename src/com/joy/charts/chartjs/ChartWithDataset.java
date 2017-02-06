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
package com.joy.charts.chartjs;

import com.joy.common.JoyParameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ChartWithDataset {
    private static String DEFAULT_COLOR = "220,220,220";
    private List<ChartDatasetPoint>   points;    // liste des groupes de bars / points
    private boolean oneColorByValue;
    private List<JoyParameter> colors;
    private String transparency;
    
    public boolean isOneColorByValue() {
        return oneColorByValue;
    }

    public void setOneColorByValue(boolean oneColorByValue) {
        this.oneColorByValue = oneColorByValue;
    }

    protected boolean isEmpty() {
        return points.isEmpty();
    } 
    
    public ChartWithDataset() {
        points = new ArrayList();
        oneColorByValue = false;
        colors = new ArrayList();
        transparency = "1";
    }
    
    public ChartWithDataset(List<JoyParameter> _Colors, String _transparency) {
        points = new ArrayList();
        oneColorByValue = false;
        transparency = _transparency;
        colors = _Colors;
    }
    
    public void add(String label, 
                    String dataset, 
                    Float Value)
    {
        points.add(new ChartDatasetPoint(label, dataset, Value));
    }
    
    /**
     * Check if a value exists in a list
     * @param myList
     * @param value
     * @return true if value is in myList
     */
    private boolean checkValueInList(List<String> myList, String value) {
        for (String val : myList) 
            if (val.equalsIgnoreCase(value))
                return true;
        return false;
    }
    
    /**
     * Returns the labels (axis for displaying)
     * @return 
     */
    private List<String> getLabels() {
        List<String> labelsUndup = new ArrayList();
        
        for (ChartDatasetPoint point : points) {
            // add unique values in label list
            if (!checkValueInList(labelsUndup, point.getLabel()))
                labelsUndup.add(point.getLabel());
        }
        return labelsUndup;
    }
    
    /**
     * Returns the labels (axis for displaying)
     * @return 
     */
    private List<String> getDatasets() {
        List<String> labelsUndup = new ArrayList();
        
        for (ChartDatasetPoint point : points) {
            // add unique values in datasets list
            if (!checkValueInList(labelsUndup, point.getDataset()))
                labelsUndup.add(point.getDataset());
        }
        return labelsUndup;
    }
    
    private String getJsonFromList(List<String> labelsUndup) {
        String result = "";
        for (String label : labelsUndup) {
            // returns the label string list
            result += "\"" + label + "\",";
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }
    
    /**
     * Get value in the points list or return 0
     * @param dataset
     * @param label
     * @return 
     */
    private Float getValue(String dataset, String label) {
        for (ChartDatasetPoint point : points) {
            if (dataset.equalsIgnoreCase(point.getDataset()) && label.equalsIgnoreCase(point.getLabel()))
                return point.getValue();
        }
        return Float.valueOf(0);
    }
    
    /**
     * Buils the values with the matching labels for the dataset
     * @param dataset
     * @param labels
     * @return 
     */
    private String getValues(String dataset, List<Float> values) {
        String result = "";
        
        for (Float val : values) {
            result += String.valueOf(val) + ",";
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }

    private String getColorByValues(List<Float> values) {
        String result = "";

        for (int i=0; i < values.size(); i++) {
            result += "\"" + getRGBA(i) + "\",";
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }
    
    /**
     * returns the data configuration
     * @return 
     */
    public String getChartData() {
        String result = "";
        List<String> labels = this.getLabels();
        List<String> datasets = this.getDatasets();
        result = "{";
        
        if (this.isEmpty())
            return "{}";
        
        // add the labels first :
        result += "\"labels\" : " + getJsonFromList(labels) + ",";

        // add the datasets afterwards
        result += "\"datasets\" : [";
        int i = 0;
        for (String dataset : datasets) {
            List<Float> values = new ArrayList();
            for (String label : labels) { // go through labels
                values.add(getValue(dataset, label));
            }
            if (i>0) result += ",";
            result += "{";
            result += "\"label\" : \"" + dataset + "\", ";
            if (oneColorByValue || datasets.size() == 1)
                result += "\"backgroundColor\" : " + this.getColorByValues(values) + ", ";
            else    
                result += "\"backgroundColor\" : \"" + getRGBA(i) + "\", ";
            result += "\"data\" : " + getValues(dataset, values);
            result += "}";
            i++;
        }
        result += "]";
        result += "}";
        return result;
    }
    
    /**
     * Return the RGBA color code for javascript
     * Needed : parameter transparency value and ChartsColors list
     *      String transparency = Joy.Parameters().getParameter("transparency").getValue().toString();
     *      List<JoyParameter> colors = Joy.Parameters().getParameter("ChartsColors").getList();
     * @param index
     * @return 
     */
    private String getRGBA(int index) {
        int i = index;
        if (transparency == null)
            transparency = "1";
        if (colors.isEmpty())
            return "rgba(" + DEFAULT_COLOR + "," + transparency + ")";
        if (i >= colors.size())
            i= 0;
        return "rgba(" + colors.get(i).getValue() + "," + transparency + ")";
    }
    
}
