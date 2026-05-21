package model;

public class Department {
    private int id;
    private String name;
    private String code;
    private int isActive;

    public Department() {}
    public Department(int id, String name, String code, int isActive){
        this.id = id; this.name = name; this.code = code; this.isActive = isActive;
    }

    // getters / setters
    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getCode(){ return code; }
    public void setCode(String code){ this.code = code; }
    public int getIsActive(){ return isActive; }
    public void setIsActive(int isActive){ this.isActive = isActive; }
}
