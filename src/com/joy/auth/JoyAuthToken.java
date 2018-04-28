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
package com.joy.auth;

import com.joy.C;
import com.joy.common.JoyClassTemplate;
import java.security.Key;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyAuthToken extends JoyClassTemplate {
    private String publicKey;
    private Date dateSession;
    private String privateKey;
    private int timeoutInMinutes;
    
    private Key generateKey() throws Exception {
        byte[] keyValue = this.privateKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyValue = sha.digest(keyValue);
        keyValue = Arrays.copyOf(keyValue, 16); // use only first 128 bit       
        Key key = new SecretKeySpec(keyValue, C.AUTH_ALGO);
        return key;
    }

    /**
     *
     * @param Data
     * @return
     * @throws Exception
     */
    protected String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(C.AUTH_ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = DatatypeConverter.printBase64Binary(encVal);
        return encryptedValue;
    }

    /**
     *
     * @param encryptedData
     * @return
     * @throws Exception
     */
    protected String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(C.AUTH_ALGO);
        c.init(Cipher.DECRYPT_MODE, key);       
        byte[] decordedValue = DatatypeConverter.parseBase64Binary(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     *
     * @return
     */
    public String getPublicKey() {
        return publicKey;
    }
    
    /**
     *
     * @param _p
     */
    public void setPublicKey(String _p) {
        publicKey = _p;
    }

    /**
     *
     * @param privateKey
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     *
     * @param timeoutInMinutes
     */
    public void setTimeoutInMinutes(int timeoutInMinutes) {
        this.timeoutInMinutes = timeoutInMinutes;
    }
    
    /**
     *
     * @return
     */
    public Date getDateSession() {
        return dateSession;
    }

    /**
     *
     */
    public JoyAuthToken() {
        this.publicKey = "";
        this.dateSession = getCurrentDate();
        this.privateKey = C.AUTH_PRIVATEKEY;
        this.timeoutInMinutes = C.AUTH_TIMEOUT;
    }
    
    private String getStrCurrentDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(C.DEFAULT_DATE_FORMAT);
        return dateFormat.format(cal.getTime());
    }
    
    private Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    /**
     *
     * @param myDate
     * @return
     */
    public Date getDateFromToken(String myDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(C.DEFAULT_DATE_FORMAT);
            return dateFormat.parse(myDate);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /**
     *
     * @return
     */
    public String buildAuthCookie() {
        try {
            return this.publicKey + "|" + this.encrypt(this.publicKey + "|" + getStrCurrentDate());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     *
     * @param _cookie
     * @return
     */
    public boolean checkAuthCookie(String _cookie) {
        try {
            JoyAuthCookie myToken = new JoyAuthCookie(_cookie);
            this.publicKey = myToken.getPublicKey();
            
            String decrypteddata = this.decrypt(myToken.getCryptedToken());
            String[] tokenparsed = decrypteddata.split("\\|");

            if (tokenparsed.length == 2) 
                return checkPublicKey(tokenparsed[0], 
                                      this.getDateFromToken(tokenparsed[1]));
            else 
                return false;
            
        } catch (Exception ex) {
            this.getLog().log(Level.WARNING, "checkToken|Exception> {0}", ex.toString());
            return false;
        }
    }
    
    private Date addMinutesToDate(int minutes, Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }

    private boolean checkPublicKey(String _publicKey, Date _date) {
        // check public keys first
        if (!_publicKey.equals(this.publicKey)) {
            getLog().log(Level.SEVERE, "Authentication failed, public keys does not match !");
            return false;
        }
        
        // Check timeout afterwards
        Date curDateLimit = addMinutesToDate(this.timeoutInMinutes, _date);
        if (curDateLimit.before(getCurrentDate())) {
            // timeout !
            getLog().log(Level.SEVERE, "Authentication failed, Timeout for this token uses");
            return false;
        }
        
        return true;
    }
    
    
}
