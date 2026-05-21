package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeAllowance {
    private int employeeAllowanceId;
    private int employeeId;

    private int allowanceTypeId;
    private String allowanceTypeName; // for UI display

    private AllowanceCalcMode calcMode;
    private BigDecimal amount;   // if AMOUNT
    private BigDecimal percent;  // if PERCENT

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    public int getEmployeeAllowanceId() { return employeeAllowanceId; }
    public void setEmployeeAllowanceId(int employeeAllowanceId) { this.employeeAllowanceId = employeeAllowanceId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getAllowanceTypeId() { return allowanceTypeId; }
    public void setAllowanceTypeId(int allowanceTypeId) { this.allowanceTypeId = allowanceTypeId; }

    public String getAllowanceTypeName() { return allowanceTypeName; }
    public void setAllowanceTypeName(String allowanceTypeName) { this.allowanceTypeName = allowanceTypeName; }

    public AllowanceCalcMode getCalcMode() { return calcMode; }
    public void setCalcMode(AllowanceCalcMode calcMode) { this.calcMode = calcMode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPercent() { return percent; }
    public void setPercent(BigDecimal percent) { this.percent = percent; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
}