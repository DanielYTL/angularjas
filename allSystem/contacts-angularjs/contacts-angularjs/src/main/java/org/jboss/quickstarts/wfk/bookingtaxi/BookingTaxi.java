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
package org.jboss.quickstarts.wfk.bookingtaxi;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
/**
 * <p>This is a the Domain object. The Booking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a bookings are retrieved from the database (with @NamedQueries), and acceptable values
 * for Booking fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = BookingTaxi.FIND_ALL, query = "SELECT c FROM BookingTaxi c ORDER BY c.id ASC"),
    @NamedQuery(name = BookingTaxi.FIND_BY_TAXIID, query = "SELECT c FROM BookingTaxi c WHERE c.taxiid.id = :taxi_id"),
   @NamedQuery(name = BookingTaxi.FIND_BY_CUSTOMER, query = "SELECT c FROM BookingTaxi c WHERE c.customer.id = :customer_id"),
    @NamedQuery(name = BookingTaxi.FIND_BY_DATE, query = "SELECT c FROM BookingTaxi c WHERE c.taxidate = :taxidate ")
})
@XmlRootElement
@Table(name = "BookingTaxi", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class BookingTaxi implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "BookingTaxi.findAll";
    public static final String FIND_BY_TAXIID = "BookingTaxi.findByTaxiid";
   public static final String FIND_BY_CUSTOMER = "BookingTaxi.findByCustomer";
   public static final String FIND_BY_DATE = "BookingTaxi.findByTaxidate";
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
 //   @Size(min =7,max = 7)
  //  @Pattern(regexp = "^[0-9]+$", message = "Please use a customer without numbers or letters")
    @JoinColumn(name = "customer_id")
    private Contact customer;


    @ManyToOne
 //   @Pattern(regexp = "^[0-9]+$",message = "Please use a number less than 22, more than 2 ")
    @JoinColumn(name = "taxi_id")
    private Taxi taxiid;
    
    
   // @Past(message = "Taxidates can not be in the future.")
   @Column(name = "taxidate")
    @Temporal(TemporalType.DATE)
    private Date taxidate;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getCustomer() {
        return customer;
    }

    public void setCustomer(Contact customer) {
        this.customer=customer;
    }

    public Taxi getTaxiid() {
        return taxiid;
    }

    public void setTaxiid(Taxi taxiid) {
        this.taxiid=taxiid;
    }

    public Date getTaxidate() {
        return taxidate;
    }

    public void setTaxidate(Date taxidate) {
        this.taxidate = taxidate;
    }
    

    
}
