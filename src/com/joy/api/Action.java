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
import com.joy.common.JoyClassTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This class manage the treatment itself, its instance is got from the JSP page directly
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class Action extends JoyClassTemplate  {
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
    
    public JoyApiRequest getCurrentRequest() {
        return state.getAPIRequest();
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

    private String getMessageEntry(String Label) {
        String msg;
        try {
            msg = m_localeBundle.getMessage(Label);
        } catch (Exception e) { msg = Label; }
        return msg;
    }
    
    public void setMessageBundle(ActionLocaleMgnt loc) {
        m_localeBundle = loc;
    }
    
    public Action() {
        super();
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
     * Set all the request PARAMETERS and attributes to the Action object 
     * @param myState
     */
    public void init(JoyState myState) {
        this.state = myState;
    }
    
    
}
