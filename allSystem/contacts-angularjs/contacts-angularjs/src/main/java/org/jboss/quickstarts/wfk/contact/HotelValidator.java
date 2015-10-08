package org.jboss.quickstarts.wfk.contact;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

public class HotelValidator {
    @Inject
    private Validator validator;

    @Inject
    private HotelRepository crud;

    /**
     * <p>Validates the given Hotel object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing hotel with the same postcode is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param contact The Hotel object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If contact with the same email already exists
     */
    void validateHotel(Hotel hotel) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        
        if (phoneAlreadyExists(hotel.getPhoneNumber(), hotel.getId())) {
            throw new ValidationException("Unique phoneNumber Violation");
        }

    }
    
    boolean phoneAlreadyExists(String phoneNumber, Long id) {
        Hotel hotel = null;
        Hotel hotelWithID = null;
        try {
        	hotel = crud.findByPhone(phoneNumber);
        } catch (NoResultException e) {
            // ignore
        }

        if (hotel != null && id != null) {
            try {
            	hotelWithID = crud.findById(id);
                if (hotelWithID != null && hotelWithID.getPhoneNumber().equals(phoneNumber)) {
                	hotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return hotel != null;
    }
}
