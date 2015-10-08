package org.jboss.quickstarts.wfk.booking;

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
import org.jboss.quickstarts.wfk.contact.Hotel;
import org.jboss.quickstarts.wfk.contact.HotelRepository;
import org.jboss.quickstarts.wfk.flight.FlightRepository;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;


public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;
    @Inject
    private ContactRepository ccrud;
    @Inject
    private HotelRepository hcrud;
    @Inject
    private TaxiRepository tcrud;
    @Inject
    private FlightRepository fcrud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     */
    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if (booking != null){
        	if((hotelAlreadyExist(booking.getHotel().getId(), booking.getId()) == false) || (customerAlreadyExist(booking.getCustomer().getId(), booking.getId()) == false))
        	{
        		if ((hotelAlreadyExist(booking.getHotel().getId(), booking.getId()) == false) && (customerAlreadyExist(booking.getCustomer().getId(), booking.getId()) == false))
        			throw new ValidationException("Unique cnh Violation");
        		else if ((hotelAlreadyExist(booking.getHotel().getId(), booking.getId()) == false))
        			throw new ValidationException("Unique hotel Violation");
        		else if ((customerAlreadyExist(booking.getCustomer().getId(), booking.getId()) == false))
        			throw new ValidationException("Unique customer Violation");
        	}
        	if (bookingExist(booking.getHotel().getId(), booking.getId(), booking.getBookingDate()))
    			throw new ValidationException("Unique booking Violation");
        }
  
    } 

    boolean customerAlreadyExist(Long customerId, Long id) {
        Contact booking = null;
        Contact bookingWithID = null;
        try {
        	booking = ccrud.findById(customerId);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
            	bookingWithID = ccrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(customerId)) {
                	booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
    
    boolean hotelAlreadyExist(Long hotelId, Long id) {
        Hotel booking = null;
        Hotel bookingWithID = null;
        try {
        	booking = hcrud.findById(hotelId);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
            	bookingWithID = hcrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(hotelId)) {
                	booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
    
    boolean bookingExist(Long hotelId, Long bookingId, Date date) {
    	List<Booking> bookings = null;
        Booking booking = null;
        try {
        	bookings = crud.findByHotelId(hotelId);
        } catch (NoResultException e) {
            // ignore
        }
        
        try{
        	for(Booking temp: bookings){
        		//System.out.println("XXXXXXXBooking");
        		System.out.println(temp.getId());
        		System.out.println(temp.getBookingDate());
        		if(temp.getBookingDate().equals(date) && temp.getId()!= bookingId)
        		   booking = temp;     		
        	}
        }catch (Exception e){
        	
        }

        return booking != null;
    }
 
  
}