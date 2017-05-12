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
package com.joy.common.filter;

import com.joy.C;
import com.joy.auth.JoyAuthToken;
import com.joy.common.state.JoyState;
import com.joy.common.JoyClassTemplate;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class FilterCommon extends JoyClassTemplate implements Filter {

    
    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void destroy() {}
    
    @Override
    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain)
    {
        JoyState joyState = null;
        
        try {
            HttpServletRequest _request = (HttpServletRequest)request;
            HttpServletResponse _response = (HttpServletResponse)response;
            
            // Check authorization here
            String headerAuth = _request.getHeader("Authorization");
            this.getLog().log(Level.WARNING, "Authorization header> {0}", headerAuth);

            // Initialization
            joyState = joyInitialize(request.getServletContext(), _request, _response);
            joyState.getLog().log(Level.FINEST, "New HTTP Request initialization, URL={0} | QueryString={1}", new Object[]{_request.getRequestURL(), _request.getQueryString()});
            
            this.process(joyState);
            
        } catch (Exception t) {
            this.getLog().log(Level.WARNING, "FilterCommon.doFilter|Exception> {0}", t.toString());
        } 
        
        joyFinalize(joyState);
        joyState = null;
    }
    
    /**
     * Check the session token validity, by default only the user name is inside
     * @param token value
     * @return true if token is valid
     */
    protected boolean checkToken(String token) {
        try {
            JoyAuthToken myToken = new JoyAuthToken(token);
            String decrypteddata = this.decrypt(myToken.getToken());
            return myToken.getUser().equals(decrypteddata);
            
        } catch (Exception ex) {
            this.getLog().log(Level.WARNING, "checkToken|Exception> {0}", ex.toString());
            return false;
        }
    }
    
    protected void process(JoyState state) {}
    
    /**
     * Check Initialization features
     * @param sce servlet context
     * @param request
     * @param response
     * @return 
     */
    protected JoyState joyInitialize(ServletContext sce, 
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        
        getLog().log(Level.FINE, "------ [JOY v{0}] Start Request Treatment ------", C.JOY_VERSION);

        // Initialisation du framework
        JoyState srvConfig = new JoyState();
        srvConfig.init(sce,request,response);
        // Token
        srvConfig.setHttpAuthToken(request.getHeader("Authorization"));
        
        // Rest configuration
        if (srvConfig.getRestConfiguration() == null)
            srvConfig.setRestConfiguration(sce.getInitParameter("joy-rest")); 
        
        // Rest configuration
        if (srvConfig.getTaskConfiguration()== null)
            srvConfig.setTaskConfiguration(sce.getInitParameter("joy-task")); 
        
        // Initialisation des fichiers de traduction
        if (!srvConfig.getMessageBundle().isInitilized()) {
            getLog().fine("[JOY] Initialize locales language and country.");
            srvConfig.getMessageBundle().setCountry(srvConfig.getAppParameters().getDefaultLocalCountry());
            srvConfig.getMessageBundle().setLanguage(srvConfig.getAppParameters().getDefaultLocalLanguage());
            srvConfig.getMessageBundle().init(srvConfig.getAppParameters().getJoyBundledMessageFile());
        }
        return srvConfig;
    }
    
    protected void joyFinalize(JoyState state) {
        try {
            getLog().log(Level.FINE, "------ [JOY v{0}]  End Of Request Treatment ------", C.JOY_VERSION);
            getLog().log(Level.FINE, "[JOY] Number of Entities cached : {0}", state.getBOFactory().cacheSize());
            getLog().log(Level.FINE, "[JOY] Treatment duration : {0} ms", state.getDuration());
            if (state != null)
                state.end();
        } catch (Exception e) {}
    }
    
    private final String ALGO = "AES";
    private  final String keyStr = "Z8LSq0wWwB5v+6YJzurcP463H3F12iZh74fDj4S74oUH4EONkiKb2FmiWUbtFh97GG/c/lbDE47mvw6j94yXxKHOpoqu6zpLKMKPcOoSppcVWb2q34qENBJkudXUh4MWcreondLmLL2UyydtFKuU9Sa5VgY/CzGaVGJABK2ZR94=";

    private Key generateKey() throws Exception {
        byte[] keyValue = keyStr.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyValue = sha.digest(keyValue);
        keyValue = Arrays.copyOf(keyValue, 16); // use only first 128 bit       
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = DatatypeConverter.printBase64Binary(encVal);
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);       
        byte[] decordedValue = DatatypeConverter.parseBase64Binary(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

}
