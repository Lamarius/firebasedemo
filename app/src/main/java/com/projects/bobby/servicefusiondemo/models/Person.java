package com.projects.bobby.servicefusiondemo.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bobby on 9/6/16.
 */
public class Person {

    private String firstName;
    private String lastName;
    private String zip;
    private Date dob;

    public Person() {

    }

    public Person(String firstName, String lastName, String zip, Date dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.zip = zip;
        this.dob = dob;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("zip", zip);
        result.put("dob", dob);

        return result;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getZip() { return zip; }

    public void setZip(String zip) { this.zip = zip; }

    public Date getDob() { return dob; }

    public void setDob(Date dob) { this.dob = dob; }
}
