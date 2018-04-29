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
 * This class manage the CRUD returns in a formatted JSON object. 
 * It's used by the framework after an update call to returns the status and messages.
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyJsonPOSTReturn extends JoyClassTemplate {
    
    /**
     *
     */
    public enum JoyEnumPOSTUpSertCodes {

        /**
         * Update request
         */
        update ("Update"),

        /**
         * Insert request
         */
        insert  ("Insert"),

        /**
         * Delete request
         */
        delete  ("Delete"),

        /**
         * Nothing !
         */
        nothing  ("Nothing"),

        /**
         * Insert or update request
         */
        upsert ("Upsert");      
        private final String name;       
        JoyEnumPOSTUpSertCodes(String s) {  name = s;  }
        @Override
        public String toString() { return this.name;  }
    }
    
    private List<String> messages;
    private int nbRowsAffected;
    private boolean status;
    private JoyEnumPOSTUpSertCodes updateType;

    /**
     * return the status of the update (OK or KO)
     * @return true if OK
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Set the status to OK
     */
    public void setStatusOk() {
        this.status = true;
    }
    
    /**
     * Set the status to KO
     */
    public void setStatusKo() {
        this.status = false;
    }
    
    /**
     * Get the update type requested
     * @return
     */
    public JoyEnumPOSTUpSertCodes getUpdateType() {
        return updateType;
    }

    /**
     * Set the update requested type 
     * @param updateType
     */
    public void setUpdateType(JoyEnumPOSTUpSertCodes updateType) {
        this.updateType = updateType;
    }
    
    /**
     * Set the Numbre of rows affected by the update (used only by the framework)
     * @param nbRowsAffected
     */
    public void setNbRowsAffected(int nbRowsAffected) {
        this.nbRowsAffected = nbRowsAffected;
    }
    
    /**
     * Initialization
     */
    public JoyJsonPOSTReturn() {
        messages = new ArrayList();
    }

    /**
     * Add a message. This is used to return errors or warning after an update
     * @param message
     */
    public void addMessage(String message) {
        messages.add (message);
    }
    
    /**
     * Return the CRUD return objet (JSON) in a structured format.
     * Example : { "status": XXX, "nbrowsaffected": XXX, "type": XXX, "messages": [ Messages here]} 
     * @return
     */
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
