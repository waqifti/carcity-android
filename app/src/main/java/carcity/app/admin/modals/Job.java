package carcity.app.admin.modals;

public class Job {
    private String id;
    private String dbEntryAt;
    private String longitude;
    private String latitude;
    private String state;
    private String description;
    private String notes;
    private String createdBy;
    private String assignedTo;
    private String assignedToCell;
    private String assignedToCurrentLongitude;
    private String assignedToCurrentLatitude;
    private String managedBy;

    public Job(String id, String dbEntryAt, String longitude, String latitude, String state, String description, String notes, String createdBy, String assignedTo, String assignedToCell, String assignedToCurrentLongitude, String assignedToCurrentLatitude, String managedBy) {
        this.id = id;
        this.dbEntryAt = dbEntryAt;
        this.longitude = longitude;
        this.latitude = latitude;
        this.state = state;
        this.description = description;
        this.notes = notes;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.assignedToCell = assignedToCell;
        this.assignedToCurrentLongitude = assignedToCurrentLongitude;
        this.assignedToCurrentLatitude = assignedToCurrentLatitude;
        this.managedBy = managedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDbEntryAt() {
        return dbEntryAt;
    }

    public void setDbEntryAt(String dbEntryAt) {
        this.dbEntryAt = dbEntryAt;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedToCell() {
        return assignedToCell;
    }

    public void setAssignedToCell(String assignedToCell) {
        this.assignedToCell = assignedToCell;
    }

    public String getAssignedToCurrentLongitude() {
        return assignedToCurrentLongitude;
    }

    public void setAssignedToCurrentLongitude(String assignedToCurrentLongitude) {
        this.assignedToCurrentLongitude = assignedToCurrentLongitude;
    }

    public String getAssignedToCurrentLatitude() {
        return assignedToCurrentLatitude;
    }

    public void setAssignedToCurrentLatitude(String assignedToCurrentLatitude) {
        this.assignedToCurrentLatitude = assignedToCurrentLatitude;
    }

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }
}
