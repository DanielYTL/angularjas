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
package org.jboss.quickstarts.wfk.bookingflight;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * <p>This is a the Domain object. The BookingFlight class represents how bookingFlight resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a bookingFlights are retrieved from the database (with @NamedQueries), and acceptable values
 * for BookingFlight fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Yutong Liu
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a Long.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = BookingFlight.FIND_ALL, query = "SELECT c FROM BookingFlight c ORDER BY c.id ASC"),
    @NamedQuery(name = BookingFlight.FIND_BY_CUSTOMERID, query = "SELECT c FROM BookingFlight c WHERE c.customerID.id = :customer_ID"),
    @NamedQuery(name = BookingFlight.FIND_BY_FLIGHTID, query = "SELECT c FROM BookingFlight c WHERE c.flightID.id = :flight_ID"),
    @NamedQuery(name = BookingFlight.FIND_BY_BOOKINGDATE, query = "SELECT c FROM BookingFlight c WHERE c.bookingFlightDate = :bookingFlight_Date")
})
@XmlRootElement
@Table(name = "BookingFlight", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class BookingFlight implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "BookingFlight.findAll";
    public static final String FIND_BY_CUSTOMERID = "BookingFlight.findByCustomerID";
    public static final String FIND_BY_FLIGHTID = "BookingFlight.findByFlightID";
    public static final String FIND_BY_BOOKINGDATE = "BookingFlight.findByBookingFlightDate";
    
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

    @ManyToOne
    @JoinColumn(name = "customer_ID")
    private Contact customerID;
    
    @ManyToOne
    @JoinColumn(name = "flight_ID")
    private Flight flightID;
    
    @NotNull
    @Future(message="Please bookingFlight date can only choose from the FUTURE")
    @Column(name = "bookingFlight_Date")
    @Temporal(TemporalType.DATE)
    private Date bookingFlightDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flight getFlightID() {
        return flightID;
    }

    public void setFlightID(Flight flightID) {
        this.flightID = flightID;
    }

    public Contact getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Contact customerID) {
        this.customerID = customerID;
    }


    public Date getBookingFlightDate() {
        return bookingFlightDate;
    }

    public void setBookingFlightDate(Date bookingFlightDate) {
        this.bookingFlightDate = bookingFlightDate;
    }
}