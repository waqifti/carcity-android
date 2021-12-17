package carcity.app.common.modals;

public class User {
    private String name;
    private String cell;
    private String type;

    public User(String name, String cell, String type) {
        this.name = name;
        this.cell = cell;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
