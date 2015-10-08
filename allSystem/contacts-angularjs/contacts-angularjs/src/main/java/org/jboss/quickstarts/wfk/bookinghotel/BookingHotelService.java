package org.jboss.quickstarts.wfk.bookinghotel;

import org.apache.http.impl.client.CloseableHttpClient;

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
public class BookingHotelService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingHotelValidator validator;

    @Inject
    private BookingHotelRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link BookingHotel} objects, sorted alphabetically by first name.<p/>
     * 
     * @return List of BookingHotel objects
     */
    List<BookingHotel> findAllOrderedById() {
        return crud.findAllOrderedById();
    }

    /**
     * <p>Returns a single BookingHotel object, specified by a Long id.<p/>
     * 
     * @param id The id field of the BookingHotel to be returned
     * @return The BookingHotel with the specified id
     */
    BookingHotel findById(Long id) {
        return crud.findById(id);
    }
    
    BookingHotel findByDate(Date bookingHotelDate) {
        return crud.findByDate(bookingHotelDate);
    }
   
    List<BookingHotel> findByCustomerId(Long customerId) {
        return crud.findByCustomerId(customerId);
    }
    List<BookingHotel> findByHotelId(Long hotelId) {
        return crud.findByHotelId(hotelId);
    }

    /**
     * <p>Writes the provided BookingHotel object to the application database.<p/>
     *
     * <p>Validates the data in the provided BookingHotel object using a {@link BookingHotelValidator} object.<p/>
     * 
     * @param bookingHotel The BookingHotel object to be written to the database using a {@link BookingHotelRepository} object
     * @return The BookingHotel object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingHotel create(BookingHotel bookingHotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingHotelService.create() - Creating " + bookingHotel.getId() + " " + bookingHotel.getCustomer() +" " + bookingHotel.getHotel());
        
        // Check to make sure the data fits with the parameters in the BookingHotel model and passes validation.
        validator.validateBookingHotel(bookingHotel);

        // Write the bookingHotel to the database.
        return crud.create(bookingHotel);
    }

    /**
     * <p>Updates an existing BookingHotel object in the application database with the provided BookingHotel object.<p/>
     *
     * <p>Validates the data in the provided BookingHotel object using a BookingHotelValidator object.<p/>
     * 
     * @param bookingHotel The BookingHotel object to be passed as an update to the application database
     * @return The BookingHotel object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingHotel update(BookingHotel bookingHotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingHotelService.update() - Updating " + bookingHotel.getId() + " " + bookingHotel.getCustomer() +" " + bookingHotel.getHotel());
        
        // Check to make sure the data fits with the parameters in the BookingHotel model and passes validation.
        validator.validateBookingHotel(bookingHotel);
        // Either update the bookingHotel or add it if it can't be found.
        return crud.update(bookingHotel);
    }

    /**
     * <p>Deletes the provided BookingHotel object from the application database if found there.<p/>
     * 
     * @param bookingHotel The BookingHotel object to be removed from the application database
     * @return The BookingHotel object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingHotel delete(BookingHotel bookingHotel) throws Exception {
        log.info("BookingHotelService.delete() - Deleting " + bookingHotel.getId() + " " + bookingHotel.getCustomer() +" " + bookingHotel.getHotel());
        
        BookingHotel deletedBookingHotel = null;
        
        if (bookingHotel.getId() != null) {
            deletedBookingHotel = crud.delete(bookingHotel);
        } else {
            log.info("BookingHotelService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedBookingHotel;
    }

}