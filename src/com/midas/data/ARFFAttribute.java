package com.midas.data;

import unalcol.types.collection.vector.Vector;


/* 
 * @author tkd 
 */
public class ARFFAttribute {

    private String name;
    private String type;
    private Vector<String> values;

    public ARFFAttribute() {
    }

    public ARFFAttribute(String name, String type, Vector values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vector<String> getValues() {
        return values;
    }

    public void setValues(Vector<String> values) {
        this.values = values;
    }

}
