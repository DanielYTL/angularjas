package org.jboss.quickstarts.wfk.bookinghotel;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BookingHotelRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;

    List<BookingHotel> findAllOrderedById() {
        TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_ALL, BookingHotel.class); 
        return query.getResultList();
    }
    
    List<BookingHotel> findByHotelId(Long hotelId) {
        TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_BY_HOTEL, BookingHotel.class).setParameter("hotelId", hotelId); 
        return query.getResultList();
    }
    
    List<BookingHotel> findByCustomerId(Long customerId) {
        TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_BY_CUSTOMER, BookingHotel.class).setParameter("customerId", customerId); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single BookingHotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the BookingHotel to be returned
     * @return The BookingHotel with the specified id
     */
    BookingHotel findById(Long id) {
        return em.find(BookingHotel.class, id);
    }
    
    BookingHotel findByDate(Date bookingHotelDate){
    	TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_BY_DATE, BookingHotel.class) .setParameter("bookingHotelDate", bookingHotelDate);
    	return query.getSingleResult();
    }
    
    BookingHotel findByHotel(Long hotelId){
    	TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_BY_HOTEL, BookingHotel.class) .setParameter("hotelId", hotelId);
    	return query.getSingleResult();
    }
    BookingHotel findByCustomer(Long customerId){
    	TypedQuery<BookingHotel> query = em.createNamedQuery(BookingHotel.FIND_BY_CUSTOMER, BookingHotel.class) .setParameter("customerId", customerId);
    	return query.getSingleResult();
    }

    
    /**
     * <p>Persists the provided BookingHotel object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param bookingHotel The BookingHotel object to be persisted
     * @return The BookingHotel object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingHotel create(BookingHotel bookingHotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingHotelRepository.create() - Creating " + bookingHotel.getId());
        
        // Write the bookingHotel to the database.
        em.persist(bookingHotel);
        
        return bookingHotel;
    }

    /**
     * <p>Updates an existing BookingHotel object in the application database with the provided BookingHotel object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param bookingHotel The BookingHotel object to be merged with an existing BookingHotel
     * @return The BookingHotel that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingHotel update(BookingHotel bookingHotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingHotelRepository.update() - Updating " + bookingHotel.getId());
        
        // Either update the bookingHotel or add it if it can't be found.
        em.merge(bookingHotel);
        
        return bookingHotel;
    }

    /**
     * <p>Deletes the provided BookingHotel object from the application database if found there</p>
     *
     * @param bookingHotel The BookingHotel object to be removed from the application database
     * @return The BookingHotel object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingHotel delete(BookingHotel bookingHotel) throws Exception {
        log.info("BookingHotelRepository.delete() - Deleting " + bookingHotel.getId());
        
        if (bookingHotel.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(), 
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent 
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it. 
             * 
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database 
             * to reattach it. 
             * 
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument. 
             * You first need an object in a persistent state to be able to delete it.
             * 
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(bookingHotel));
            
        } else {
            log.info("BookingHotelRepository.delete() - No ID was found so can't Delete.");
        }
        
        return bookingHotel;
    }

}