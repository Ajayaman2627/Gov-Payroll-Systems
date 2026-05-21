package model;

import java.math.BigDecimal;

public class PayrollRecord {
    private int recordId;
    private int runId;
    private int empId;
    private String empName;
    private String deptName;
    private String gradeName;
    private int stepNo;
    private BigDecimal basicSalary;
    private BigDecimal totalAllowances;
    private BigDecimal grossPay;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getRunId() { return runId; }
    public void setRunId(int runId) { this.runId = runId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
    public int getStepNo() { return stepNo; }
    public void setStepNo(int stepNo) { this.stepNo = stepNo; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    public BigDecimal getTotalAllowances() { return totalAllowances; }
    public void setTotalAllowances(BigDecimal totalAllowances) { this.totalAllowances = totalAllowances; }
    public BigDecimal getGrossPay() { return grossPay; }
    public void setGrossPay(BigDecimal grossPay) { this.grossPay = grossPay; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }
    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
}
