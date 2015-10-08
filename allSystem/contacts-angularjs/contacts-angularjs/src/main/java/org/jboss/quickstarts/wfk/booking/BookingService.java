package org.jboss.quickstarts.wfk.booking;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.quickstarts.wfk.bookingflight.BookingFlight;
import org.jboss.quickstarts.wfk.bookingtaxi.BookingTaxi;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class BookingService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingValidator validator;

    @Inject
    private BookingRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by first name.<p/>
     * 
     * @return List of Booking objects
     */
    List<Booking> findAllOrderedById() {
        return crud.findAllOrderedById();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    Booking findById(Long id) {
        return crud.findById(id);
    }
    
    Booking findByDate(Date bookingDate) {
        return crud.findByDate(bookingDate);
    }
   
    List<Booking> findByCustomerId(Long customerId) {
        return crud.findByCustomerId(customerId);
    }
    List<Booking> findByHotelId(Long hotelId) {
        return crud.findByHotelId(hotelId);
    }
    Booking findAllByTaxi(Long taxiid) {
        return crud.findByTaxi(taxiid);
    }
    Booking findAllByFlight(Long flightID) {
        return crud.findByFlight(flightID);
    }
    

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     * 
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking create(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingService.create() - Creating " + booking.getId() + " " + booking.getCustomer() +" " + booking.getHotel() + " " + booking.getTaxiid()+ " " +booking.getFlightID());
        
        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(booking);

        // Write the booking to the database.
        return crud.create(booking);
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.<p/>
     *
     * <p>Validates the data in the provided Booking object using a BookingValidator object.<p/>
     * 
     * @param booking The Booking object to be passed as an update to the application database
     * @return The Booking object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking update(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingService.update() - Updating " + booking.getId() + " " + booking.getCustomer() +" " + booking.getHotel() + " " + booking.getTaxiid()+ " " +booking.getFlightID());
        
        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(booking);
        // Either update the booking or add it if it can't be found.
        return crud.update(booking);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     * 
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingService.delete() - Deleting " + booking.getId() + " " + booking.getCustomer() +" " + booking.getHotel() + " " + booking.getTaxiid()+ " " +booking.getFlightID());
        
        Booking deletedBooking = null;
        
        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("BookingService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedBooking;
    }

}