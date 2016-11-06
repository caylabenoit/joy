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
import com.joy.json.JSONObject;
import com.joy.mvc.actionTypes.ActionTypeTASK;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyTaskManager {
    private List<ActionTypeTASK> tasks;     // tasks threads
    private int lastTaskId;
    public void init() {
        // Task Manager initialization here
        lastTaskId = 1;
        tasks = new ArrayList();
    }
    
    private int setNewId() {
        return lastTaskId++;
    }
    
    /**
     * Create a new task
     * @param taskObject
     * @param taskName
     * @param className
     * @param mySession
     * @param request
     * @param entities
     * @return 
     */
    public boolean newTask(String taskObject,
                           String taskName,
                           String className,
                           HttpSession mySession, 
                           HttpServletRequest request,
                           BOFactory entities) {
        // start a new task / thread
        try {
            Joy.log().debug("Create new Task for " + taskName);
            ActionTypeTASK taskThread = (ActionTypeTASK) Class.forName(className).newInstance();
            taskThread.setRequest(request);
            if (taskObject == null)
                taskObject = "No Task Name";
            taskThread.setTaskObject(taskObject);
            taskThread.setSession(mySession);
            taskThread.setTaskManager(this);
            taskThread.setTaskName(taskName);
            taskThread.setEntities(entities);
            taskThread.setTaskId(this.setNewId());
            Joy.log().debug("Start Task id " + taskThread.getId());
            taskThread.start();
            tasks.add(taskThread);
            Joy.log().debug("Task started");
            
            return true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Joy.log().fatal(e);
            return false;
        }
    }
    
    private String taskStatus(JoyTaskStatus status) {
        switch (status) {
            case Running: return "Running"; 
            case Failed:  return "Failed"; 
            case Success: return "Success";  
            case Paused:  return "Paused";
            default: return "Undefined";
        }
    }
    
    /**
     * Return a Task in JSON format
     * @param task
     * @return 
     */
    private JSONObject getJsonTask(JoyTask task) {
        JSONObject oneTask = new JSONObject();
        oneTask.put("id", task.getTaskId());
        oneTask.put("name", (task.getTaskName() == null ? "No Name" : task.getTaskName()));
        oneTask.put("object", task.getTaskObject());
        oneTask.put("status", taskStatus(task.getStatus()));
        oneTask.put("message", task.getMessage());
        oneTask.put("start", task.getStringStartDatetime());
        oneTask.put("end", task.getStringEndDatetime());
        oneTask.put("duration", task.getDurationInSeconds());

        // add the traces
        Collection<JSONObject> colTraces = new ArrayList<>();
        for (String trace : task.getTraces()) {
            JSONObject myTrace = new JSONObject();
            myTrace.put("message", trace);
            colTraces.add(myTrace);
        }
        oneTask.put("trace", colTraces);
        
        return oneTask;
    }
    
    public String getJSONTasksDesc(int limit) {
        try {
            int li = 0;
            Collection<JSONObject> myList = new ArrayList<>();
            for (int i=tasks.size()-1; i>=0; i--) {
                if (limit != 0 && ++li >= limit)
                    return myList.toString();
                myList.add(getJsonTask(tasks.get(i)));
            }
            return myList.toString();
            
        } catch (Exception e) {
            Joy.log().error(e);
            return "";
        }
    }
    
    /**
     * Return the JSON task list
     * @return 
     */
    public String getJSONTasks(int limit) {
        try {
            int i = 0;
            Collection<JSONObject> myList = new ArrayList<>();
            
            for (JoyTask task : tasks) {
                if (limit != 0 && ++i >= limit)
                    return myList.toString();
                myList.add(getJsonTask(task));
            }
            return myList.toString();
            
        } catch (Exception e) {
            Joy.log().error(e);
            return "";
        }
    }
    
    public List<ActionTypeTASK> getTasks() {
        return tasks;
    }
    
    public ActionTypeTASK getTask(int id) {
        for (ActionTypeTASK task : tasks) 
            if (task.getId() == id)
                return task;
        return null;
    }
}
