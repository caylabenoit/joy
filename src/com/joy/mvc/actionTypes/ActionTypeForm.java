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
package com.joy.mvc.actionTypes;

import com.joy.C;
import com.joy.mvc.ActionForm;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeForm extends ActionForm {

    public String edit() {
        return C.ATYPE_FORM_EDIT;
    }
    
    public String update() {
        return C.ATYPE_FORM_UPDATE;
    }
    
    public String delete() {
        return C.ATYPE_FORM_DELETE;
    }
    
    public String add() {
        return C.ATYPE_FORM_ADD;
    }
    
    public String list() {
        return C.ATYPE_FORM_LIST;
    }
    
    public String other() {
        return C.ATYPE_FORM_OTHER;
    }
    
    public String display() {
        return C.ATYPE_FORM_DISPLAY;
    }
    
    public String execute (String ActionType) {
        return C.ATYPE_FORM_OTHER;
    }
    
    public String search () {
        return C.ATYPE_FORM_SEARCH;
    }
    
    public String nodata () {
        return C.ATYPE_FORM_NODATA;
    }
}
