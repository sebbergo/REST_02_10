package DTO;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

public class PersonDTO {

    private String fName;
    private String lName;
    private String phone;
    private long id;
    private String street;
    private String zip;
    private String city;
    
    List<PersonDTO> all = new ArrayList();

    public PersonDTO(Person p) {
        this.fName = p.getfName();
        this.lName = p.getlName();
        this.phone = p.getPhone();
        this.id = p.getId();
        this.street = p.getAddress().getStreet();
        this.zip = p.getAddress().getZip();
        this.city = p.getAddress().getCity();
    }
    
    public PersonDTO(String fn, String ln, String phone) {
        this.fName = fn;
        this.lName = ln;
        this.phone = phone;        
    }

    public PersonDTO(List<Person> personEntities) {
        personEntities.forEach((p) -> {
            all.add(new PersonDTO(p));
        });
    }
    
    public PersonDTO() {
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<PersonDTO> getAll() {
        return all;
    }

    public void setAll(List<PersonDTO> all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "PersonDTO{" + "fName=" + fName + ", lName=" + lName + ", phone=" + phone + ", id=" + id + ", all=" + all + '}';
    }
}
