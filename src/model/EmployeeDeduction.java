package model;

public class EmployeeDeduction {
    private int employeeDeductionId;
    private int employeeId;
    private int deductionTypeId;
    private String deductionMode; // AMOUNT or PERCENT
    private double deductionValue;
    private boolean active;

    public EmployeeDeduction() {
    }

    public EmployeeDeduction(int employeeDeductionId, int employeeId, int deductionTypeId,
                             String deductionMode, double deductionValue, boolean active) {
        this.employeeDeductionId = employeeDeductionId;
        this.employeeId = employeeId;
        this.deductionTypeId = deductionTypeId;
        this.deductionMode = deductionMode;
        this.deductionValue = deductionValue;
        this.active = active;
    }

    public int getEmployeeDeductionId() {
        return employeeDeductionId;
    }

    public void setEmployeeDeductionId(int employeeDeductionId) {
        this.employeeDeductionId = employeeDeductionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getDeductionTypeId() {
        return deductionTypeId;
    }

    public void setDeductionTypeId(int deductionTypeId) {
        this.deductionTypeId = deductionTypeId;
    }

    public String getDeductionMode() {
        return deductionMode;
    }

    public void setDeductionMode(String deductionMode) {
        this.deductionMode = deductionMode;
    }

    public double getDeductionValue() {
        return deductionValue;
    }

    public void setDeductionValue(double deductionValue) {
        this.deductionValue = deductionValue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}