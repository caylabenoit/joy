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

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyAuthCookie extends JoyClassTemplate {
    private String publicKey;
    private String cryptedToken;

    /**
     *
     * @return
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     *
     * @return
     */
    public String getCryptedToken() {
        return cryptedToken;
    }

    /**
     *
     * @param globalToken
     */
    public JoyAuthCookie(String globalToken) {
        String[] tokenparsed = globalToken.split("\\|");
        
        if (tokenparsed.length == 2) {
            this.publicKey = tokenparsed[0];
            this.cryptedToken = tokenparsed[1];
        } else {
            this.publicKey = C.DEFAULT_USER;
            this.cryptedToken = "";
        }
    }

}
