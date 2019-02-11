package com.chinatelecom.util;

public enum Primary {
    
    primary(true), minor(false);
    
    private boolean isPrimary;
    
    private Primary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public boolean getPrimary() {
        return isPrimary;
    }
    
}
