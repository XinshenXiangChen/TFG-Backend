package generated;

import java.util.List;

/**
 * Generated from UML metamodel class Doctor.
 */
public class Doctor extends Person {

    private String nif;
    private String medicalLicense;
    private List<Appointment> appointments;

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getMedicalLicense() {
        return medicalLicense;
    }

    public void setMedicalLicense(String medicalLicense) {
        this.medicalLicense = medicalLicense;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

}
