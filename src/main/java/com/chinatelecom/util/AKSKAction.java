package com.chinatelecom.util;

public enum AKSKAction {
    
    create("CreateAccessKey"), delete("DeleteAccessKey"), update("UpdateAccessKey"), list("ListAccessKey");
    
    private String name;
    
    private AKSKAction(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

}
