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

import com.joy.Joy;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyReadStream implements Runnable {
    private String name;
    private InputStream is;
    private Thread thread;   
    private String cmdreturn;

    public String getCmdreturn() {
        return cmdreturn;
    }
    
    public JoyReadStream(String name, InputStream is) {
        this.name = name;
        this.is = is;
    }       
    
    public void start () {
        thread = new Thread (this);
        thread.start ();
    }       
    
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);   
            while (true) {
                String s = br.readLine ();
                if (s == null) break;
                cmdreturn = s;
                //log4jProvider.LOG("JoyReadStream.run()", "Command Returning > [" + name + "] " + cmdreturn);
            }
            is.close ();  
            
        } catch (Exception ex) {
            Joy.LOG().error("Command Returning > [" + name + "] " + cmdreturn);
            Joy.LOG().error("Exception > " + ex);
            ex.printStackTrace ();
        }
    }
}