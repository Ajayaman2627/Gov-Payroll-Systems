package model;

import java.math.BigDecimal;

public class PayStructure {
    private int id;
    private int gradeId;
    private String gradeName;
    private int stepNo;
    private BigDecimal salaryAmount;

    public PayStructure() {}

    public PayStructure(int id, int gradeId, String gradeName, int stepNo, BigDecimal salaryAmount) {
        this.id = id;
        this.gradeId = gradeId;
        this.gradeName = gradeName;
        this.stepNo = stepNo;
        this.salaryAmount = salaryAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
    public int getStepNo() { return stepNo; }
    public void setStepNo(int stepNo) { this.stepNo = stepNo; }
    public BigDecimal getSalaryAmount() { return salaryAmount; }
    public void setSalaryAmount(BigDecimal salaryAmount) { this.salaryAmount = salaryAmount; }
}
