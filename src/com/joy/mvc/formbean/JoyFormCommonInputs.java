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
package com.joy.mvc.formbean;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyFormCommonInputs {
    private String name;
    private JoyFormInputTypes inputType;
    protected String id;
    
    public JoyFormInputTypes getInputType() {
        return inputType;
    }

    public void setInputType(JoyFormInputTypes InputType) {
        this.inputType = InputType;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public JoyFormCommonInputs(String Name, 
                               JoyFormInputTypes InputType) {
        this.name = Name;
        this.inputType = InputType;
    }

    public JoyFormCommonInputs() {
        this.name="";
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

}
