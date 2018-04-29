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
package com.joy.common;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionLogReport {

    /**
     *
     */
    static public enum enum_CRITICITY {

        /**
         *
         */
        ERROR,

        /**
         *
         */
        INFO,

        /**
         *
         */
        WARNING,

        /**
         *
         */
        FATAL
    }
    private String label;
    private int code;
    private String description;
    private enum_CRITICITY criticity;

    /**
     *
     * @param Label
     * @param Code
     * @param Description
     * @param Criticity
     */
    public ActionLogReport(String Label, int Code, String Description, enum_CRITICITY Criticity) {
        this.label = Label;
        this.code = Code;
        this.description = Description;
        this.criticity = Criticity;
    }

    /**
     *
     * @param Label
     */
    public ActionLogReport(String Label) {
        this.label = Label;
        this.code = 0;
        this.description = description;
        this.criticity = criticity;
    }
    
    /**
     *
     */
    public ActionLogReport() {
        this.label = "";
        this.code = 0;
        this.description = "";
        this.criticity = enum_CRITICITY.INFO;
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
     * @param Label
     */
    public void setLabel(String Label) {
        this.label = Label;
    }

    /**
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @param Code
     */
    public void setCode(int Code) {
        this.code = Code;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param Description
     */
    public void setDescription(String Description) {
        this.description = Description;
    }

    /**
     *
     * @return
     */
    public enum_CRITICITY getCriticity() {
        return criticity;
    }

    /**
     *
     * @param Criticity
     */
    public void setCriticity(enum_CRITICITY Criticity) {
        this.criticity = Criticity;
    }
    
}
