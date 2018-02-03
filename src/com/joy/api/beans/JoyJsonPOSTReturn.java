/*
 * Copyright (C) 2017 Benoit Cayla (benoit@famillecayla.fr)
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
package com.joy.api.beans;

import com.joy.common.JoyClassTemplate;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyJsonPOSTReturn extends JoyClassTemplate {
    
    public enum JoyEnumPOSTUpSertCodes {
        update ("Update"),
        insert  ("Insert"),
        delete  ("Delete"),
        nothing  ("Nothing"),
        upsert ("Upsert");      
        private final String name;       
        JoyEnumPOSTUpSertCodes(String s) {  name = s;  }
        public String toString() { return this.name;  }
    }
    
    private List<String> messages;
    private int nbRowsAffected;
    private boolean status;
    private JoyEnumPOSTUpSertCodes updateType;

    public boolean isStatus() {
        return status;
    }

    public void setStatusOk() {
        this.status = true;
    }
    
    public void setStatusKo() {
        this.status = false;
    }
    
    public JoyEnumPOSTUpSertCodes getUpdateType() {
        return updateType;
    }

    public void setUpdateType(JoyEnumPOSTUpSertCodes updateType) {
        this.updateType = updateType;
    }
    
    public void setNbRowsAffected(int nbRowsAffected) {
        this.nbRowsAffected = nbRowsAffected;
    }
    
    public JoyJsonPOSTReturn() {
        messages = new ArrayList();
    }

    public void addMessage(String message) {
        messages.add (message);
    }
    
    public JSONObject getJsonReturn() {
        try {
            JSONObject main = new  JSONObject();

            main.put("status", (this.status ? "OK" : "KO"));
            main.put("nbrowsaffected", this.nbRowsAffected);
            main.put("type", this.updateType.toString());
            
            JSONArray msgs = new  JSONArray();
            for (String msg : messages) {
                msgs.put(msg);
            }
            main.put("messages", msgs);

            return main;
            
        } catch (Exception e) {
            this.getLog().severe(e.toString());
            return null;
        }
    }
    
}
