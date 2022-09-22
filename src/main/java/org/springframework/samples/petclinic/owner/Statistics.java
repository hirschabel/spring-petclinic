package org.springframework.samples.petclinic.owner;

class Statistics {
    private String szoveg;
    private double average;

    public Statistics(String szoveg, double average) {
        this.szoveg = szoveg;
        this.average = average;
    }

    public double getAverage() {
        return average;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }
}
