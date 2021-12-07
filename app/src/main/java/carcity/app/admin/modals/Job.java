package carcity.app.admin.modals;

public class Job {
    private String id;
    private String dbentryat;
    private String longi;
    private String lati;
    private String state;
    private String description;
    private String notes;
    private String createdby;
    private String assignedto;
    private String assignedtodetails;
    private String managedby;

    public Job(String id, String dbentryat, String longi, String lati, String state, String description, String notes, String createdby, String assignedto, String assignedtodetails, String managedby) {
        this.id = id;
        this.dbentryat = dbentryat;
        this.longi = longi;
        this.lati = lati;
        this.state = state;
        this.description = description;
        this.notes = notes;
        this.createdby = createdby;
        this.assignedto = assignedto;
        this.assignedtodetails = assignedtodetails;
        this.managedby = managedby;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDbentryat() {
        return dbentryat;
    }

    public void setDbentryat(String dbentryat) {
        this.dbentryat = dbentryat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
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

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getAssignedto() {
        return assignedto;
    }

    public void setAssignedto(String assignedto) {
        this.assignedto = assignedto;
    }

    public String getAssignedtodetails() {
        return assignedtodetails;
    }

    public void setAssignedtodetails(String assignedtodetails) {
        this.assignedtodetails = assignedtodetails;
    }

    public String getManagedby() {
        return managedby;
    }

    public void setManagedby(String managedby) {
        this.managedby = managedby;
    }
}
