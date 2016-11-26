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
package com.joy.tasks;

import com.joy.Joy;
import com.joy.bo.BOFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyTask extends Thread {
    private HttpSession session;            // http session
    private HttpServletRequest request;     // http request
    private JoyTaskManager taskManager;     // reference to the task manager
    private String taskName;                // task name (given by the P1 parameter)
    private BOFactory entities;            // ENTITIES
    private String taskObject;              // Task type, is equal to the first parameter of thhp query (and the joy Object tag)
    private int taskId;                     // Task ID, is given by the task manager
    private JoyTaskStatus status;           // Task status
    private String message;                 // return message
    private Date startDatetime;             // time start
    private Date endDatetime;               // time end
    private List<String> trace;

    public JoyTask() {
        trace = new ArrayList();
    }
    
    public void addTrace(String message) {
        trace.add(message);
    }
    
    public List<String> getTraces() {
        return trace;
    }
    
    public void end() {
        entities.End();
        session = null;
        request = null;
        endDatetime = new Date();
    }
    
    public long getDurationInSeconds() {
        try {
            long diff = endDatetime.getTime() - startDatetime.getTime();
            return diff / 1000;      
        } catch (Exception e) {
            return -1;
        }
    }
    
    public Date getStartDatetime() {
        return startDatetime;
    }
    
    public String getStringStartDatetime() {
        return Joy.DATE_FORMAT(startDatetime);
    }
    
    public Date getEndDatetime() {
        return endDatetime;
    }
    
    public String getStringEndDatetime() {
        return Joy.DATE_FORMAT(endDatetime);
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String Message) {
        this.message = Message;
    }
    
    public void resetMessages() {
        this.message = "";
    }
    
    public void addMessage(String Message) {
        this.message += Message;
    }
    
    public String getTaskObject() {
        return taskObject;
    }

    public void setTaskObject(String taskObject) {
        this.taskObject = taskObject;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    
    public BOFactory getEntities() {
        return entities;
    }

    public void setEntities(BOFactory entities) {
        this.entities = entities;
    }
    
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public JoyTaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(JoyTaskManager taskManager) {
        this.taskManager = taskManager;
    }
    
    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession mySession) {
        this.session = mySession;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    @Override
    public void run() {
        startDatetime = new Date();
        
        message = "Running";
        JoyTaskStatus myStatus;
        status = JoyTaskStatus.Running;
        myStatus = taskExecute();
        this.end();
        super.run(); //To change body of generated methods, choose Tools | Templates.
        status = myStatus;
    }
    
    /**
     * MUST OVERRIDE with the good script
     * @return by default returns Success
     */
    public JoyTaskStatus taskExecute() {
        // to override !
        return JoyTaskStatus.Success;
    }
    
    public JoyTaskStatus getStatus() {
        if (status == JoyTaskStatus.Running && !this.isAlive()) {
            return JoyTaskStatus.Failed;
        }
        if (this.isInterrupted()) {
            return JoyTaskStatus.Paused;
        }
        return status;
    }
}
