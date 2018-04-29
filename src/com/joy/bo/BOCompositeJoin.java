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
package com.joy.bo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOCompositeJoin {
    private List<BOCompositeJoinKeys> keys;
    private String slaveEntity;
    private String slaveEntityAlias;
    private boolean slaveIsAQuery;
    private String joinType;

    /**
     *
     * @return
     */
    public boolean isSlaveAQuery() {
        return slaveIsAQuery;
    }

    /**
     *
     * @return
     */
    public String getSlaveEntityAlias() {
        return slaveEntityAlias;
    }

    /**
     *
     * @return
     */
    public String getJoinType() {
        return joinType;
    }

    /**
     *
     * @param RightEntity
     * @param JoinType
     * @param RightIsAQuery
     * @param RightEntityAlias
     */
    public BOCompositeJoin(String RightEntity, String JoinType, boolean RightIsAQuery, String RightEntityAlias) {
        this.slaveEntity = RightEntity;
        this.joinType = JoinType;
        this.slaveIsAQuery = RightIsAQuery;
        this.slaveEntityAlias = RightEntityAlias;
        keys = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public List<BOCompositeJoinKeys> getKeys() {
        return keys;
    }

    /**
     *
     * @param leftField
     * @param rightField
     */
    public void addKeys(String leftField, String rightField) {
        keys.add(new BOCompositeJoinKeys(leftField, rightField));
    }

    /**
     *
     * @return
     */
    public String getSlaveEntity() {
        return slaveEntity;
    }

    /**
     *
     * @param RightEntity
     */
    public void setSlaveEntity(String RightEntity) {
        this.slaveEntity = RightEntity;
    }
    
}
