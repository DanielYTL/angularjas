package org.jboss.quickstarts.wfk.bookinghotel;

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


public class BookingHotelValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingHotelRepository crud;
    @Inject
    private ContactRepository ccrud;
    @Inject
    private HotelRepository hcrud;

    /**
     * <p>Validates the given BookingHotel object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     * @param bookingHotel The BookingHotel object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     */
    void validateBookingHotel(BookingHotel bookingHotel) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<BookingHotel>> violations = validator.validate(bookingHotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if (bookingHotel != null){
        	if((hotelAlreadyExist(bookingHotel.getHotel().getId(), bookingHotel.getId()) == false) || (customerAlreadyExist(bookingHotel.getCustomer().getId(), bookingHotel.getId()) == false))
        	{
        		if ((hotelAlreadyExist(bookingHotel.getHotel().getId(), bookingHotel.getId()) == false) && (customerAlreadyExist(bookingHotel.getCustomer().getId(), bookingHotel.getId()) == false))
        			throw new ValidationException("Unique cnh Violation");
        		else if ((hotelAlreadyExist(bookingHotel.getHotel().getId(), bookingHotel.getId()) == false))
        			throw new ValidationException("Unique hotel Violation");
        		else if ((customerAlreadyExist(bookingHotel.getCustomer().getId(), bookingHotel.getId()) == false))
        			throw new ValidationException("Unique customer Violation");
        	}
        	if (bookingHotelExist(bookingHotel.getHotel().getId(), bookingHotel.getId(), bookingHotel.getBookingHotelDate()))
    			throw new ValidationException("Unique bookingHotel Violation");
        }
        
      /* if(hotelAlreadyExists(bookingHotel.getHotel(), bookingHotel.getId(), bookingHotel.getBookingHotelDate())){
        	throw new ValidationException("Unique bookingHotelDate Violation");
        }
   
        if(customerAlreadyExists(bookingHotel.getCustomer(), bookingHotel.getId(), bookingHotel.getBookingHotelDate())){
        	throw new ValidationException("Unique Customer BookingHotel Violation");
        }
        */
  
    } 
/**
    boolean hotelAlreadyExists(Hotel hotel, Long id, Date bookingHotelDate) {
        BookingHotel bookingHotel = null;
        BookingHotel bookingHotelWithID = null;
        try {
        	bookingHotel = crud.findByDate(bookingHotelDate);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingHotel != null && id != null) {
            try {
            	bookingHotelWithID = crud.findByDate(bookingHotelDate);
                if (bookingHotelWithID != null && bookingHotelWithID.getBookingHotelDate().equals(bookingHotelDate) && bookingHotelWithID.getHotel().equals(hotel)) {
                	bookingHotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingHotel != null;
    }
 
    boolean customerAlreadyExists(Contact customer, Long id, Date bookingHotelDate) {
        BookingHotel bookingHotel = null;
        BookingHotel bookingHotelWithID = null;
        try {
        	bookingHotel = crud.findByDate(bookingHotelDate);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingHotel != null && id != null) {
            try {
            	bookingHotelWithID = crud.findByDate(bookingHotelDate);
                if (bookingHotelWithID != null && bookingHotelWithID.getBookingHotelDate().equals(bookingHotelDate) && bookingHotelWithID.getCustomer().equals(customer)) {
                	bookingHotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingHotel != null;
    }
  */  
    boolean customerAlreadyExist(Long customerId, Long id) {
        Contact bookingHotel = null;
        Contact bookingHotelWithID = null;
        try {
        	bookingHotel = ccrud.findById(customerId);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingHotel != null && id != null) {
            try {
            	bookingHotelWithID = ccrud.findById(id);
                if (bookingHotelWithID != null && bookingHotelWithID.getId().equals(customerId)) {
                	bookingHotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingHotel != null;
    }
    
    boolean hotelAlreadyExist(Long hotelId, Long id) {
        Hotel bookingHotel = null;
        Hotel bookingHotelWithID = null;
        try {
        	bookingHotel = hcrud.findById(hotelId);
        } catch (NoResultException e) {
            // ignore
        }

        if (bookingHotel != null && id != null) {
            try {
            	bookingHotelWithID = hcrud.findById(id);
                if (bookingHotelWithID != null && bookingHotelWithID.getId().equals(hotelId)) {
                	bookingHotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return bookingHotel != null;
    }
    
    boolean bookingHotelExist(Long hotelId, Long bookingHotelId, Date date) {
    	List<BookingHotel> bookingHotels = null;
        BookingHotel bookingHotel = null;
        try {
        	bookingHotels = crud.findByHotelId(hotelId);
        } catch (NoResultException e) {
            // ignore
        }
        
        try{
        	for(BookingHotel temp: bookingHotels){
        		//System.out.println("XXXXXXXBookingHotel");
        		System.out.println(temp.getId());
        		System.out.println(temp.getBookingHotelDate());
        		if(temp.getBookingHotelDate().equals(date) && temp.getId()!= bookingHotelId)
        		   bookingHotel = temp;     		
        	}
        }catch (Exception e){
        	
        }

        return bookingHotel != null;
    }
 
  
}