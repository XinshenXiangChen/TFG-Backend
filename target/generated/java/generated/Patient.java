package generated;

import java.util.List;

/**
 * Generated from UML metamodel class Patient.
 */
public class Patient extends Person {

    private String nif;
    private String healthCardId;
    private List<Appointment> appointments;

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getHealthCardId() {
        return healthCardId;
    }

    public void setHealthCardId(String healthCardId) {
        this.healthCardId = healthCardId;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

}
