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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRepository;

/**
 * <p>This class provides methods to check BookingFlight objects against arbitrary requirements.</p>
 * 
 * @author Yutong Liu
 * @see BookingFlight
 * @see BookingFlightRepository
 * @see javax.validation.Validator
 */
public class BookingFlightValidator {
    @Inject
    private Validator validator;
    // validator use repository for bookingFlight, contact and flight
    @Inject
    private BookingFlightRepository bcrud;
    @Inject
    private ContactRepository cbcrud;
    @Inject
    private FlightRepository fbcrud;

    /**
     * <p>Validates the given BookingFlight object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing bookingFlight with the same customerID is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param bookingFlight The BookingFlight object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If bookingFlight with the same customerID already exists
     */
    void validateBookingFlight(BookingFlight bookingFlight) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<BookingFlight>> violations = validator.validate(bookingFlight);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if(bookingFlight!=null)
        {
        if ((flightNotExists(bookingFlight.getFlightID().getId(), bookingFlight.getId())==false)||(customerExists(bookingFlight.getCustomerID().getId(), bookingFlight.getId())==false)||bookingFlightExists(bookingFlight.getFlightID().getId(), bookingFlight.getId(),bookingFlight.getBookingFlightDate())) 
        {
        	if (bookingFlightExists( bookingFlight.getFlightID().getId(),bookingFlight.getId(),bookingFlight.getBookingFlightDate()))
        		throw new ValidationException("Unique bookingFlight Violation");
        	 if ((flightNotExists(bookingFlight.getFlightID().getId(), bookingFlight.getId())==false)&&(customerExists(bookingFlight.getCustomerID().getId(), bookingFlight.getId())==false)) 
        		throw new ValidationException("Unique btc Violation");
        	else if ((customerExists(bookingFlight.getCustomerID().getId(), bookingFlight.getId())==false))
        		throw new ValidationException("Unique customerID Violation");
        	else if ((flightNotExists(bookingFlight.getFlightID().getId(), bookingFlight.getId())==false))
        		throw new ValidationException("Unique flightID Violation");	
        	} 
        } 
        // Check the uniqueness of the customerID address
       /* if (bookingFlightDateAlreadyExists(bookingFlight.getBookingFlightDate(), bookingFlight.getId())) {
            throw new ValidationException("Unique CustomerID Violation");
        }*/
    }
    
    boolean customerExists(	Long customerID, Long id) {
        Contact bookingFlight = null;
        Contact bookingFlightWithID = null;
        try {
            bookingFlight = cbcrud.findById(customerID);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingFlight != null && id != null) {
            try {
                bookingFlightWithID = cbcrud.findById(id);
                if (bookingFlightWithID != null && bookingFlightWithID.getId().equals(customerID)) {
                    bookingFlight = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingFlight != null;
    }
    
    boolean flightExists(Long flightID, Long id) {
        BookingFlight bookingFlight = null;
        BookingFlight bookingFlightWithID = null;
        try {
            bookingFlight = bcrud.findByFlightID(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingFlight != null && id != null) {
            try {
                bookingFlightWithID = bcrud.findById(id);
                if (bookingFlightWithID != null && bookingFlightWithID.getFlightID().getId().equals(flightID)) {
                    bookingFlight = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingFlight != null;
    }
    
    boolean flightNotExists(Long flightID, Long id) {
        Flight bookingFlight = null;
        Flight bookingFlightWithID = null;
        try {
            bookingFlight = fbcrud.findById(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingFlight != null && id != null) {
            try {
                bookingFlightWithID = fbcrud.findById(id);
                if (bookingFlightWithID != null && bookingFlightWithID.getId().equals(flightID)) {
                    bookingFlight = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingFlight != null;
    }

    /**
     * <p>Checks if a bookingFlight with the same customerID address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "customerID")" constraint from the BookingFlight class.</p>
     * 
     * <p>Since Update will being using an customerID that is already in the database we need to make sure that it is the customerID
     * from the record being updated.</p>
     * 
     * @param customerID The customerID to check is unique
     * @param id The user id to check the customerID against if it was found
     * @return boolean which represents whether the customerID was found, and if so if it belongs to the user with id
     */
    boolean dateExists(Date bookingFlightDate, Long id) {
    	
        BookingFlight bookingFlight = null;
        BookingFlight bookingFlightWithID = null;
        try {
        	bookingFlight = bcrud.findByBookingFlightDate(bookingFlightDate);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingFlight != null && id != null) {
            try {
                bookingFlightWithID = bcrud.findById(id);
                if (bookingFlightWithID != null && bookingFlightWithID.getBookingFlightDate().equals(bookingFlightDate)) {
                    bookingFlight = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingFlight != null;
    }
    boolean bookingFlightExists(Long flightID, Long bookingFlightId, Date date) {
        List<BookingFlight> bookingFlights = null;
        BookingFlight bookingFlight = null;
        try {
            bookingFlights = bcrud.findListByFlightID(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        try {
        	for (BookingFlight temp: bookingFlights)
            {
        		System.out.println(temp.getId());
        		System.out.println(temp.getBookingFlightDate());
            	if (temp.getBookingFlightDate().equals(date)&&temp.getId()!=bookingFlightId)
            		bookingFlight=temp;
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
        return bookingFlight != null;
    }
}
