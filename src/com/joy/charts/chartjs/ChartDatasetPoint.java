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

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)  
 */
public class ChartDatasetPoint {
    private String label;
    private String dataset;
    private Float value;    // liste des groupes de bars

    /**
     *
     * @param label
     * @param dataset
     * @param value
     */
    public ChartDatasetPoint(String label, String dataset, Float value) {
        this.label = label;
        this.dataset = dataset;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     */
    public String getDataset() {
        return dataset;
    }

    /**
     *
     * @param dataset
     */
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    /**
     *
     * @return
     */
    public Float getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(Float value) {
        this.value = value;
    }


}
