package carcity.app.common.utils;

public class JobDetails {
    private int id;
    private String dbEntryAt;
    private double longitudeCustomer;
    private double latitudeCustomer;
    private String state;
    private String description;
    private String notes;
    private String createdBy;
    private String assignedTo;
    private String assignedToCell;
    private double assignedToCurrentLongitude;
    private double assignedToCurrentLatitude;
    private String managedBy;

    public JobDetails(){}

    public JobDetails(int id, String dbEntryAt, double longitudeCustomer, double latitudeCustomer, String state, String description, String notes, String createdBy, String assignedTo, String assignedToCell, double assignedToCurrentLongitude, double assignedToCurrentLatitude, String managedBy) {
        this.id = id;
        this.dbEntryAt = dbEntryAt;
        this.longitudeCustomer = longitudeCustomer;
        this.latitudeCustomer = latitudeCustomer;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbEntryAt() {
        return dbEntryAt;
    }

    public void setDbEntryAt(String dbEntryAt) {
        this.dbEntryAt = dbEntryAt;
    }

    public double getLongitudeCustomer() {
        return longitudeCustomer;
    }

    public void setLongitudeCustomer(double longitudeCustomer) {
        this.longitudeCustomer = longitudeCustomer;
    }

    public double getLatitudeCustomer() {
        return latitudeCustomer;
    }

    public void setLatitudeCustomer(double latitudeCustomer) {
        this.latitudeCustomer = latitudeCustomer;
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

    public double getAssignedToCurrentLongitude() {
        return assignedToCurrentLongitude;
    }

    public void setAssignedToCurrentLongitude(double assignedToCurrentLongitude) {
        this.assignedToCurrentLongitude = assignedToCurrentLongitude;
    }

    public double getAssignedToCurrentLatitude() {
        return assignedToCurrentLatitude;
    }

    public void setAssignedToCurrentLatitude(double assignedToCurrentLatitude) {
        this.assignedToCurrentLatitude = assignedToCurrentLatitude;
    }

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }
}
