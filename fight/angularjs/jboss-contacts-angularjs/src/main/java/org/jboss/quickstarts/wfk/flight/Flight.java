/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.flight;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * <p>This is a the Domain object. The fight class represents how fight resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a fights are retrieved from the database (with @NamedQueries), and acceptable values
 * for fight fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Flight.FIND_ALL, query = "SELECT c FROM Flight c ORDER BY c.flightNumber ASC, c.flightDeparture ASC, c.flightDestination ASC"),
    @NamedQuery(name = Flight.FIND_BY_FLIGHTNUMBER, query = "SELECT c FROM Flight c WHERE c.flightNumber = :flightNumber"),
   
})
@XmlRootElement
@Table(name = "Flight", uniqueConstraints = @UniqueConstraint(columnNames = "flight_number"))
public class Flight implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "Flight.findAll";
    public static final String FIND_BY_FLIGHTNUMBER = "Flight.findByFlightnumber";

    /*
     * The  error messages match the ones in the UI so that the user isn't confused by two similar error messages for
     * the same error after hitting submit. This is if the form submits while having validation errors. The only
     * difference is that there are no periods(.) at the end of these message sentences, this gives us a way to verify
     * where the message came from.
     * 
     * Each variable name exactly matches the ones used on the HTML form name attribute so that when an error for that
     * variable occurs it can be sent to the correct input field on the form.  
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "Please input a flight number")
    @Column(name = "flight_number")
    private String flightNumber;
    
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$", message = "Please input a valid departure")
    @Column(name = "flight_departure")
    private String flightDeparture;
    
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$", message = "Please input a valid destination")
    @Column(name = "flight_destination")
    private String flightDestination;
    
  
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
    	return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
    	this.flightNumber = flightNumber;
    }
    
    public String getFlightDeparture() {
    	return flightDeparture;
    }
    
    public void setFlightDeparture(String flightDeparture) {
    	this.flightDeparture = flightDeparture;
    }
    
    public String getFlightDestination() {
    	return flightDestination;
    }
    
    public void setFlightDestination(String flightDestination) {
    	this.flightDestination = flightDestination;
    }
   
}