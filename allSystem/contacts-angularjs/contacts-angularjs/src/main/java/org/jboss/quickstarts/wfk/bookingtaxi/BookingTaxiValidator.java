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
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;

/**
 * <p>This class provides methods to check BookingTaxi objects against arbitrary requirements.</p>
 * 
 * @author Joshua Wilson
 * @see BookingTaxi
 * @see BookingTaxiRepository
 * @see javax.validation.Validator
 */
public class BookingTaxiValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingTaxiRepository crud;
    @Inject
    private ContactRepository ccrud;
    @Inject
    private TaxiRepository tcrud;

    /**
     * <p>Validates the given BookingTaxi object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing bookingTaxi with the same customer is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param bookingTaxi The BookingTaxi object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If bookingTaxi with the same customer already exists
     */
    void validateBookingTaxi(BookingTaxi bookingTaxi) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<BookingTaxi>> violations = validator.validate(bookingTaxi);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }  
        if(bookingTaxi!=null)
        {
        if ((taxiNotExists(bookingTaxi.getTaxiid().getId(), bookingTaxi.getId())==false)||(customerExists(bookingTaxi.getCustomer().getId(), bookingTaxi.getId())==false)||bookingTaxiExists(bookingTaxi.getTaxiid().getId(), bookingTaxi.getId(),bookingTaxi.getTaxidate())) 
        {
        	if (bookingTaxiExists( bookingTaxi.getTaxiid().getId(),bookingTaxi.getId(),bookingTaxi.getTaxidate()))
        		throw new ValidationException("Unique bookingTaxi Violation");
        	 if ((taxiNotExists(bookingTaxi.getTaxiid().getId(), bookingTaxi.getId())==false)&&(customerExists(bookingTaxi.getCustomer().getId(), bookingTaxi.getId())==false)) 
        		throw new ValidationException("Unique btc Violation");
        	else if ((customerExists(bookingTaxi.getCustomer().getId(), bookingTaxi.getId())==false))
        		throw new ValidationException("Unique customer Violation");
        	else if ((taxiNotExists(bookingTaxi.getTaxiid().getId(), bookingTaxi.getId())==false))
        		throw new ValidationException("Unique taxiid Violation");	
        	} 
        } 
        }
   

    /**
     * <p>Checks if a bookingTaxi with the same customer address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "customer")" constraint from the BookingTaxi class.</p>
     * 
     * <p>Since Update will being using an customer that is already in the database we need to make sure that it is the customer
     * from the record being updated.</p>
     * 
     * @param customer The customer to check is unique
     * @param id The user id to check the customer against if it was found
     * @return boolean which represents whether the customer was found, and if so if it belongs to the user with id
     */
    
    boolean customerExists(	Long customer, Long id) {
        Contact bookingTaxi = null;
        Contact bookingTaxiWithID = null;
        try {
            bookingTaxi = ccrud.findById(customer);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingTaxi != null && id != null) {
            try {
                bookingTaxiWithID = ccrud.findById(id);
                if (bookingTaxiWithID != null && bookingTaxiWithID.getId().equals(customer)) {
                    bookingTaxi = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingTaxi != null;
    }
      
    
    boolean taxiExists(Long taxiid, Long id) {
        BookingTaxi bookingTaxi = null;
        BookingTaxi bookingTaxiWithID = null;
        try {
            bookingTaxi = crud.findByTaxiid(taxiid);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingTaxi != null && id != null) {
            try {
                bookingTaxiWithID = crud.findById(id);
                if (bookingTaxiWithID != null && bookingTaxiWithID.getTaxiid().getId().equals(taxiid)) {
                    bookingTaxi = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingTaxi != null;
    }
    
    boolean taxiNotExists(	Long taxiid, Long id) {
        Taxi bookingTaxi = null;
        Taxi bookingTaxiWithID = null;
        try {
            bookingTaxi = tcrud.findById(taxiid);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingTaxi != null && id != null) {
            try {
                bookingTaxiWithID = tcrud.findById(id);
                if (bookingTaxiWithID != null && bookingTaxiWithID.getId().equals(taxiid)) {
                    bookingTaxi = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingTaxi != null;
    }
    
    
    
    
    boolean dateExists(Date taxidate, Long id) {
    	
        BookingTaxi bookingTaxi = null;
        BookingTaxi bookingTaxiWithID = null;
        try {
        	//String sdate1;
            //sdate1=(new SimpleDateFormat("yyyy-MM-dd")).format(taxidate);
            bookingTaxi =crud.findByTaxidate(taxidate);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingTaxi != null && id != null) {
            try {
                bookingTaxiWithID = crud.findById(id);
                if (bookingTaxiWithID != null && bookingTaxiWithID.getTaxidate().equals(taxidate)) {
                    bookingTaxi = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingTaxi != null;
    }
    
    boolean bookingTaxiExists(Long taxiid, Long bookingTaxiId, Date date) {
        List<BookingTaxi> bookingTaxis = null;
        BookingTaxi bookingTaxi = null;
        try {
            bookingTaxis = crud.findListByTaxiid(taxiid);
        } catch (NoResultException e) {
            // ignore
        }

        try {
        	for (BookingTaxi temp: bookingTaxis)
            {
        		System.out.println("XXXXXXXXBookingTaxi");
        		System.out.println(temp.getId());
        		System.out.println(temp.getTaxidate());
            	if (temp.getTaxidate().equals(date)&&temp.getId()!=bookingTaxiId)
            		bookingTaxi=temp;
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
        return bookingTaxi != null;
    }
}

