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
import com.joy.mvc.Action;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeAsync extends Action {
    protected String resultOut;
    protected String title;
    protected boolean hasBeenLaunched;
    protected int status;

    public boolean isHasBeenLaunched() {
        return hasBeenLaunched;
    }

    public void setHasBeenLaunched(boolean hasBeenLaunched) {
        this.hasBeenLaunched = hasBeenLaunched;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResultOut() {
        return resultOut;
    }

    public void setResultOut(String resultOut) {
        this.resultOut = resultOut;
    }
   
    // Must override this function
    public void execute() {
        this.setHasBeenLaunched(true);

        try {
            Joy.LOG().debug ( "End of execution !");
            this.setResultOut(null);

        } catch (Exception e) {
            Joy.LOG().error (  e.toString());
            this.setResultOut(e.toString());
        }
        Joy.LOG().debug ( "End of execution !");
    }
    
    public boolean beforeWait() {
        return true;
    }
    
    public boolean isSuccessful() {
        return true;
    }
}
