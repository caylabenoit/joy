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

    public boolean isSlaveAQuery() {
        return slaveIsAQuery;
    }

    public String getSlaveEntityAlias() {
        return slaveEntityAlias;
    }

    public String getJoinType() {
        return joinType;
    }

    public BOCompositeJoin(String RightEntity, String JoinType, boolean RightIsAQuery, String RightEntityAlias) {
        this.slaveEntity = RightEntity;
        this.joinType = JoinType;
        this.slaveIsAQuery = RightIsAQuery;
        this.slaveEntityAlias = RightEntityAlias;
        keys = new ArrayList<>();
    }

    public List<BOCompositeJoinKeys> getKeys() {
        return keys;
    }

    public void addKeys(String leftField, String rightField) {
        keys.add(new BOCompositeJoinKeys(leftField, rightField));
    }

    public String getSlaveEntity() {
        return slaveEntity;
    }

    public void setSlaveEntity(String RightEntity) {
        this.slaveEntity = RightEntity;
    }
    
}
