package com.company;

class Node {
    private boolean infected;
    private boolean new_infected;
    private boolean immune = false;

    boolean isImmune() {
        return immune;
    }

    void setImmune() {
        this.immune = true;
    }

    boolean isInfected() {
        return infected;
    }

    void fixNewState() {
        infected = new_infected;
    }

    void setNew_infected(boolean new_infected) {
        if (!isImmune()) {
            this.new_infected = new_infected;
        } else {
            this.new_infected = false;
        }
    }
}