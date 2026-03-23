package model;

public class Subject {
    private int id;
    private String name;
    private String code;
    private int credits;

    public Subject(int id, String name, String code, int credits) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credits = credits;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", credits=" + credits +
                '}';
    }
}
