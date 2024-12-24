import java.time.LocalDate;

public class Employee {
    private String name;
    private int id;
    private String position;
    private String department;
    private double salary;
    private LocalDate hireDate;

    public Employee(String name, int id, String position, String department, double salary, LocalDate hireDate) {
        this.name = name;
        this.id = id;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public double calculateAnnualSalary() {
        return salary * 12;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                '}';
    }
}
