package org.jboss.quickstarts.wfk.contact;


import org.apache.http.impl.client.CloseableHttpClient;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.logging.Logger;

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class HotelService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private HotelValidator validator;

    @Inject
    private HotelRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by hotel name.<p/>
     * 
     * @return List of Hotel objects
     */
    List<Hotel> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String HotelName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param hotelName The hotelName field of the Hotel to be returned
     * @return The first Hotel with the specified hotelName
     */
    Hotel findByHotelName(String hotelName) {
        return crud.findByHotelName(hotelName);
    }
    
    Hotel findByPhone(String phoneNumber){
    	return crud.findByPhone(phoneNumber);
    }

    /**
     * <p>Writes the provided Hotel object to the application database.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a {@link HotelValidator} object.<p/>
     * 
     * @param hotel The Hotel object to be written to the database using a {@link HotelRepository} object
     * @return The Hotel object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel create(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.create() - Creating " + hotel.getHotelName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(hotel);

        // Write the hotel to the database.
        return crud.create(hotel);
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a HotelValidator object.<p/>
     * 
     * @param hotel The Hotel object to be passed as an update to the application database
     * @return The Hotel object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel update(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.update() - Updating " + hotel.getHotelName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(hotel);

        // Either update the hotel or add it if it can't be found.
        return crud.update(hotel);
    }
    
    Hotel delete(Hotel hotel){
        System.out.println("Hotel records can not be deleted!");
        return hotel;
    }

}