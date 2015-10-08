package org.jboss.quickstarts.wfk.booking;

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
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 * 
 * @author Yutong Liu
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;
    // validator use repository for booking, contact and flight
    @Inject
    private BookingRepository bcrud;
    @Inject
    private ContactRepository cbcrud;
    @Inject
    private FlightRepository fbcrud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing booking with the same customerID is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If booking with the same customerID already exists
     */
    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if(booking!=null)
        {
        if ((flightNotExists(booking.getFlightID().getId(), booking.getId())==false)||(customerExists(booking.getCustomerID().getId(), booking.getId())==false))
        {
        	if (bookingExists( booking.getFlightID().getId(),booking.getId(),booking.getBookingDate()))
        		throw new ValidationException("Unique booking Violation");
        	 if ((flightNotExists(booking.getFlightID().getId(), booking.getId())==false)&&(customerExists(booking.getCustomerID().getId(), booking.getId())==false)) 
        		throw new ValidationException("Unique btc Violation");
        	else if ((customerExists(booking.getCustomerID().getId(), booking.getId())==false))
        		throw new ValidationException("Unique customerID Violation");
        	else if ((flightNotExists(booking.getFlightID().getId(), booking.getId())==false))
        		throw new ValidationException("Unique flightID Violation");	
        	}
            if (bookingExists(booking.getFlightID().getId(), booking.getId(),booking.getBookingDate()))
                throw new ValidationException("Unique bookingHotel Violation");
        } 
        // Check the uniqueness of the customerID address
       /* if (bookingDateAlreadyExists(booking.getBookingDate(), booking.getId())) {
            throw new ValidationException("Unique CustomerID Violation");
        }*/
    }
    
    boolean customerExists(	Long customerID, Long id) {
        Contact booking = null;
        Contact bookingWithID = null;
        try {
            booking = cbcrud.findById(customerID);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = cbcrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(customerID)) {
                    booking = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
   /*
    boolean flightExists(Long flightID, Long id) {
        Booking booking = null;
        Booking bookingWithID = null;
        try {
            booking = bcrud.findByFlightID(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = bcrud.findById(id);
                if (bookingWithID != null && bookingWithID.getFlightID().getId().equals(flightID)) {
                    booking = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }*/
    
    boolean flightNotExists(Long flightID, Long id) {
        Flight booking = null;
        Flight bookingWithID = null;
        try {
            booking = fbcrud.findById(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = fbcrud.findById(id);
                if (bookingWithID != null && bookingWithID.getId().equals(flightID)) {
                    booking = null;
                }
                
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }

    /**
     * <p>Checks if a booking with the same customerID address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "customerID")" constraint from the Booking class.</p>
     * 
     * <p>Since Update will being using an customerID that is already in the database we need to make sure that it is the customerID
     * from the record being updated.</p>
     * 
     * @param customerID The customerID to check is unique
     * @param id The user id to check the customerID against if it was found
     * @return boolean which represents whether the customerID was found, and if so if it belongs to the user with id
     */

    
    
    
    boolean bookingExists(Long flightID, Long bookingId, Date date) {
        List<Booking> bookings = null;
        Booking booking = null;
        try {
            bookings = bcrud.findListByFlightID(flightID);
        } catch (NoResultException e) {
            // ignore
        }

        try {
        	for (Booking temp: bookings)
            {
        		System.out.println(temp.getId());
        		System.out.println(temp.getBookingDate());
            	if (temp.getBookingDate().equals(date)&&temp.getId()!=bookingId)
            		booking=temp;
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
        return booking != null;
    }
}
