package model;

import java.math.BigDecimal;

public class AllowanceType {

    // Real DB-based fields
    private int allowanceTypeId;
    private String name;
    private String calcType;
    private BigDecimal defaultValue;
    private boolean taxable;

    // Compatibility fields for older UI screens
    private String description;
    private boolean active;

    public AllowanceType() {
        this.defaultValue = BigDecimal.ZERO;
        this.description = "";
        this.active = true;
    }

    // Old constructor used by older UI code
    public AllowanceType(int id, String name, String description, boolean active) {
        this.allowanceTypeId = id;
        this.name = name;
        this.description = description;
        this.active = active;

        // keep DB fields safe
        this.calcType = "AMOUNT";
        this.defaultValue = BigDecimal.ZERO;
        this.taxable = false;
    }

    // Optional full constructor for DB-based usage
    public AllowanceType(int allowanceTypeId, String name, String calcType, BigDecimal defaultValue, boolean taxable) {
        this.allowanceTypeId = allowanceTypeId;
        this.name = name;
        this.calcType = calcType;
        this.defaultValue = defaultValue;
        this.taxable = taxable;

        // compatibility defaults
        this.description = "";
        this.active = true;
    }

    public int getAllowanceTypeId() {
        return allowanceTypeId;
    }

    public void setAllowanceTypeId(int allowanceTypeId) {
        this.allowanceTypeId = allowanceTypeId;
    }

    // compatibility aliases
    public int getId() {
        return allowanceTypeId;
    }

    public void setId(int id) {
        this.allowanceTypeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Real DB field
    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    // Real DB field
    public BigDecimal getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(BigDecimal defaultValue) {
        this.defaultValue = defaultValue;
    }

    // Real DB field
    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    // Compatibility methods for old UI
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name;
    }
}