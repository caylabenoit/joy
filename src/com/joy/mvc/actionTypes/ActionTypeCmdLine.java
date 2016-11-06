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

import com.joy.Joy;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeCmdLine extends ActionTypeAsync {
    private String commandLine;

    public boolean hasCommandLine() {
        return (!commandLine.isEmpty());
    }
   
    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public void execute() {
        Joy.log().info ( "Request to Launch command line");
        
        try {
            String cmd = this.getCommandLine();
            this.setHasBeenLaunched(true);
            this.setResultOut(Joy.executeCommandLine(cmd));
            
            Joy.log().debug ( "End of execution !");
        } catch (Exception ex) {
            Joy.log().error( ex);
        } 
    }
    
}
