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
package com.joy.etl;

import com.joy.C;
import com.joy.JOY;
import com.joy.common.JoyClassTemplate;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class MappingFactory extends JoyClassTemplate {
    private org.jdom2.Document m_document;
    private Element m_racine;
    
    /**
     * Initialize the mappings with the xml configuration file 
     * @param filename
     * @return 
     */
    public boolean init(String filename)  {
        try {
            //m_document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
            m_document = JOY.OPEN_XML(filename);
            m_racine = m_document.getRootElement();
            return (m_racine != null) ;
            
        } catch (Exception ex) {
            getLog().severe("Exception=" + ex );
            getLog().severe( "The file " + filename + " may not found !");
        } 
        return false;
    }
    
    /**
     * Return the mapping specification
     * @param name mapping name
     * @return 
     */
    public MappingSpecification getMapping(String name) {
        List level1list = m_racine.getChildren(C.PARAMETERS_TAG_MAPPING);
        Iterator i1 = level1list.iterator();
        while(i1.hasNext()) { 
            Element elt = (Element)i1.next();
            if (getNodeAttr(elt, C.ID).equalsIgnoreCase(name)) {
                // get the good mapping
                MappingSpecification mapping = new MappingSpecification(elt.getAttributeValue(C.ID));
                mapping.setFrom(getNodeAttr(elt, C.JOTYMAP_TAG_ENTITYFROM));
                mapping.setTo(getNodeAttr(elt, C.JOTYMAP_TAG_ENTITYTO));
                mapping.setFilter(getNodeAttr(elt, C.JOTYMAP_TAGATTR_FILTER));
                MapAllFields(elt, mapping);
                return mapping;
            }
        }
        return null;
    }
    
    /**
     * Get all the mapping informations through the xml file
     * @param elt xml node containing the mappin information
     * @param mapping 
     */
    private void MapAllFields(Element elt, 
                              MappingSpecification mapping) {
        List level1list;
        
        try {
            // Single maps management
            level1list = elt.getChildren(C.JOTYMAP_TAG_MAP);
            Iterator i1 = level1list.iterator();
            while(i1.hasNext()) { 
                Element node = (Element)i1.next();

                FieldMap field = new FieldMap(getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_FROM), 
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_TO), 
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_KEY).equalsIgnoreCase(C.YES),
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_CHECKEX).equalsIgnoreCase(C.YES),
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_VALUE),
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_DEFNULL),
                                              getNodeAttr(node, C.JOTYMAP_TAGATTR_MAP_TYPE));
                mapping.add(field);
            }

            // lookup management
            level1list = elt.getChildren(C.JOTYMAP_TAG_LOOKUP);
            i1 = level1list.iterator();
            while (i1.hasNext()) {
                Element node = (Element)i1.next();
                LookupMap map = new LookupMap();
                map.setTo(node.getAttributeValue(C.JOTYMAP_TAGATTR_MAP_TO));
                map.setEntityLookup(node.getChild(C.JOTYMAP_TAG_ENTITYLOOKUP).getText());
                map.setEntityLookupKey(node.getChild(C.JOTYMAP_TAG_ENTITYLOOKUP_KEY).getText());
                map.addCondition(getNodeAttr(node.getChild(C.JOTYMAP_TAG_LOOKUP_COND), C.JOTYMAP_TAGATTR_MAP_FROM), 
                                 getNodeAttr(node.getChild(C.JOTYMAP_TAG_LOOKUP_COND), C.JOTYMAP_TAG_LOOKUP_KEY));
                mapping.add(map);
            }
        } catch (Exception e) {
            getLog().severe( "Exception=" + e);
        }
    }
    
    private String getNodeAttr(Element node, String name) {
        String retVal = "";
        try {
            retVal = node.getAttributeValue(name);
        } catch (Exception e) {
            retVal = "";
        }
        return (retVal == null ? "" : retVal);
    }
    
}
