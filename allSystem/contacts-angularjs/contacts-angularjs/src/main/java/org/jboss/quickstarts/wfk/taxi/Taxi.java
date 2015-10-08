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
package org.jboss.quickstarts.wfk.taxi;

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

/**
 * <p>This is a the Domain object. The Taxi class represents how taxi resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a taxis are retrieved from the database (with @NamedQueries), and acceptable values
 * for Taxi fields (with @NotNull, @Pattern etc...)<p/>
 * 
 * @author Joshua Wilson
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT c FROM Taxi c ORDER BY c.seat ASC"),
    @NamedQuery(name = Taxi.FIND_BY_REGISTRATION, query = "SELECT c FROM Taxi c WHERE c.registration = :registration")
})
@XmlRootElement
@Table(name = "Taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registration"))
public class Taxi implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REGISTRATION = "Taxi.findByRegistration";

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
    @Size(min =7,max = 7)
    @Pattern(regexp = "^[A-Za-z0-9]{7}$", message = "Please use a registration without numbers or letters")
    @Column(name = "registration")
    private String registration;


    @NotNull
    @Pattern(regexp = "^([1]?[0-1]|[0-1]?[2-9]|20|21|22)$",message = "Please use a number less than 22, more than 2 ")
    @Column(name = "seat")
    private String seat;
      
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }   

    
}
