
package org.jboss.quickstarts.wfk.contact;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.logging.Logger;

public class HotelRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by name.</p>
     * 
     * @return List of Hotel objects
     */
    List<Hotel> findAllOrderedByName() {
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_ALL, Hotel.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    public Hotel findById(Long id) {
        return em.find(Hotel.class, id);
    }
    
    Hotel findByPhone(String phoneNumber) {
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_BY_PHONE, Hotel.class) .setParameter("phoneNumber", phoneNumber);
        return query.getSingleResult();
    }

    /**
     * <p>Returns a single Hotel object, specified by a String hotelName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param hotelName The hotelName field of the Hotel to be returned
     * @return The first Hotel with the specified hotelName
     */
    Hotel findByHotelName(String hotelName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> criteria = cb.createQuery(Hotel.class);
        Root<Hotel> hotel = criteria.from(Hotel.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(hotel).where(cb.equal(hotel.get(Hotel_.hotelName), hotelName));
        criteria.select(hotel).where(cb.equal(hotel.get("hotelName"), hotelName));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Persists the provided Hotel object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param hotel The Hotel object to be persisted
     * @return The Hotel object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel create(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelRepository.create() - Creating " + hotel.getHotelName());
        
        // Write the hotel to the database.
        em.persist(hotel);
        
        return hotel;
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param hotel The Hotel object to be merged with an existing Hotel
     * @return The Hotel that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel update(Hotel hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelRepository.update() - Updating " + hotel.getHotelName());
        
        // Either update the hotel or add it if it can't be found.
        em.merge(hotel);
        
        return hotel;
    }
    Hotel delete(Hotel hotel) throws Exception {
        System.out.println("Hotels can not be deleted!");
        return hotel;
    }

}