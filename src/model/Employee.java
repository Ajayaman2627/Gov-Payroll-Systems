package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee {
    public int empId;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public Date dob;
    public Date hireDate;
    public BigDecimal basicSalary;

    public int deptId;
    public int gradeId;
    public int stepNo;
    public int isActive;

    // For displaying names in the table
    public String deptName;
    public String gradeName;

    public Employee() {}

    // New full constructor (includes grade + step)
    public Employee(int empId, String firstName, String lastName, String email, String phone,
                    Date dob, Date hireDate, BigDecimal basicSalary,
                    int deptId, int gradeId, int stepNo, int isActive,
                    String deptName, String gradeName) {

        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.hireDate = hireDate;
        this.basicSalary = basicSalary;
        this.deptId = deptId;
        this.gradeId = gradeId;
        this.stepNo = stepNo;
        this.isActive = isActive;
        this.deptName = deptName;
        this.gradeName = gradeName;
    }

    // Old constructor kept so older code won’t break (deptName only)
    public Employee(int empId, String firstName, String lastName, String email, String phone,
                    Date dob, Date hireDate, BigDecimal basicSalary, int deptId, int isActive, String deptName) {
        this(empId, firstName, lastName, email, phone, dob, hireDate, basicSalary,
                deptId, 0, 1, isActive, deptName, "");
    }
}