package com.googlecode.jsendnsca;

public class PerfDatum {

    public enum UOM {
        Unspecified (""),
        Seconds ("s"),
        Percentage ("%"),
        Bytes ("B"),
        Kilobytes ("KB"),
        Megabytes ("MB"),
        Terabytes ("TB"),
        Counter ("c");

        private final String symbol;

        UOM(String symbol){
            this.symbol = symbol;
        }

        public String symbol(){
            return this.symbol;
        }
    }

    public PerfDatum(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public PerfDatum(String label, Object value, UOM unitOfMeasurement) {
        this.label = label;
        this.value = value;
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public PerfDatum(String label, Object value, Object warn, Object critical, Object min, Object max) {
        this.label = label;
        this.value = value;
        this.warn = warn;
        this.critical = critical;
        this.min = min;
        this.max = max;
    }

    public PerfDatum(String label, Object value, UOM unitOfMeasurement, Object warn, Object critical, Object min, Object max) {
        this.label = label;
        this.value = value;
        this.unitOfMeasurement = unitOfMeasurement;
        this.warn = warn;
        this.critical = critical;
        this.min = min;
        this.max = max;
    }

    String label;

    Object value;

    Object warn;

    Object critical;

    Object min;

    Object max;

    UOM unitOfMeasurement = UOM.Unspecified;

    public String getLabel() {
        return label;
    }

    public Object getValue() {
        return value;
    }

    public Object getWarn() {
        return warn;
    }

    public Object getCritical() {
        return critical;
    }

    public Object getMin() {
        return min;
    }

    public Object getMax() {
        return max;
    }

    public UOM getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(label).append("=").append(value).append(unitOfMeasurement.symbol());

        if (warn != null && critical != null && min != null && max != null)
            sb.append(";").append(warn).append(";").append(critical).append(";").append(min).append(";").append(max);

        return sb.toString();
    }
}
