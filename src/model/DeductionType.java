package model;

public class DeductionType {
    private int deductionTypeId;
    private String deductionName;

    public DeductionType() {
    }

    public DeductionType(int deductionTypeId, String deductionName) {
        this.deductionTypeId = deductionTypeId;
        this.deductionName = deductionName;
    }

    public int getDeductionTypeId() {
        return deductionTypeId;
    }

    public void setDeductionTypeId(int deductionTypeId) {
        this.deductionTypeId = deductionTypeId;
    }

    public String getDeductionName() {
        return deductionName;
    }

    public void setDeductionName(String deductionName) {
        this.deductionName = deductionName;
    }

    @Override
    public String toString() {
        return deductionName;
    }
}