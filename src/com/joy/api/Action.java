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
package com.joy.api;

import com.joy.common.state.JoyState;
import com.joy.bo.BOFactory;
import com.joy.common.ActionLogReport;
import com.joy.common.joyClassTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This class manage the treatment itself, its instance is got from the JSP page directly
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class Action extends joyClassTemplate  {
    private List m_DisplayMessages;       // List of messages to return
    private ActionLocaleMgnt m_localeBundle; 
    private List<FileItem> m_AttachedFiles;
    private JoyState state;
    
    public BOFactory getBOFactory() {
        return state.getBOFactories().get(0);
    }
    
    public BOFactory getBOFactory(int index) {
        return state.getBOFactories().get(index);
    }
    
    public JoyState getState() {
        return state;
    }
    
    public void endOfWork() {}
    
    public HttpServletRequest getCurrentRequest() {
        return state.getCurrentRequest();
    }
    
    public StringBuffer getURL() {
        return state.getCurrentRequest().getRequestURL();
    }
    
    public boolean hasAttachedFiles() {
        return (m_AttachedFiles != null);
    }
    
    public List<FileItem> attachedFiles() {
        return m_AttachedFiles;
    }
    
    public InputStream attachedFile(int Index) throws IOException {
        return m_AttachedFiles.get(Index).getInputStream();
    }
    
    public String getURI() {
        String tmpURI = state.getCurrentRequest().getRequestURI();
        String[] uriParts = tmpURI.split("/");
        return "/" + uriParts[1];
    }
    
    public void addDisplayMessageError(String Label) {
        m_DisplayMessages.add(new ActionLogReport(Label, 0, getMsg(Label), ActionLogReport.enum_CRITICITY.ERROR));
    }
    
    private String getMsg(String Label) {
        String msg;
        try {
            msg = m_localeBundle.getMessage(Label);
        } catch (Exception e) { msg = Label; }
        return msg;
    }
    
    public void addDisplayMessageInfo(String Label) {
        m_DisplayMessages.add(new ActionLogReport(Label, 0, getMsg(Label), ActionLogReport.enum_CRITICITY.INFO));
    }
    
    public List<ActionLogReport> getAllDisplayMessages() {
        return m_DisplayMessages;
    }
    
    public void setMessageBundle(ActionLocaleMgnt loc) {
        m_localeBundle = loc;
    }

    public String getMessage (String _Label) {
        if (m_localeBundle != null)
            return m_localeBundle.getMessage(_Label);
        else
            return null;
    }
    
    public Action() {
        super();
        this.m_DisplayMessages = new ArrayList();
        this.m_localeBundle = null;
        this.state = null;
    }
    
    /**
     * Collect attached files
     * @param request 
     */
    public void collectAttachedFiles(HttpServletRequest request) { 
        // crée un nouveau multipart parser, la taille des objets parsés étant de 10 MB au maximum 
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        
        if (isMultipart) { 
            try {
                // il y a des fichiers dans la requete

                // Create a factory for disk-based file items & Set factory constraints
                DiskFileItemFactory factory = new DiskFileItemFactory();
                //factory.setSizeThreshold(yourMaxMemorySize);
                //factory.setRepository(yourTempDirectory);

                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                // Parse the request
                m_AttachedFiles = upload.parseRequest(request);

            } catch (FileUploadException ex) {
                getLog().severe("Error while getting multipart data");
            }
        }
    }
    
    /**
     * Set all the request PARAMETERS & attributes to the Action object
     * @param CurrentRequest 
     */
    public void init(JoyState myState) {
        this.state = myState;
    }
    
    public void setNewArgument(String _name, Object _value) {
        state.getCurrentRequest().setAttribute(_name, _value);
    }
    
    private Object getArgumentValue(String _name) {
        Object retObj = null;
        
        // try first to get a parameter
        try {
            retObj = this.state.getCurrentRequest().getParameter(_name);
        } catch (Exception e) {
            retObj = null;
        }       
        
        // else .. try first to get an attribute
        if (retObj == null) {
            try {
                retObj = this.state.getCurrentRequest().getAttribute(_name);
            } catch (Exception e) {
                retObj = null;
            }    
        }
        return (retObj == null ? "" : retObj);
    }
    
    public String getStrArgumentValue(String _name) {
        return getArgumentValue(_name).toString();
    }
    
    public int getIntArgumentValue(String _name) {
        String val = getArgumentValue(_name).toString();
        try {
            return Integer.parseInt(val); 
        } catch (Exception e) {
            return 0;
        }
    }
}
