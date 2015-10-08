package org.jboss.quickstarts.wfk.flight;

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

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link FlightService} with the
 * Domain/Entity Object (see {@link Flight}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Yutong Liu
 * @see Flight
 * @see javax.persistence.EntityManager
 */
public class FlightRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Flight} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of fight objects
     */
    List<Flight> findAllOrderedByName() {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_ALL, Flight.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single fight object, specified by a Long id.<p/>
     *
     * @param id The id field of the fight to be returned
     * @return The fight with the specified id
     */
    public Flight findById(Long id) {
        return em.find(Flight.class, id);
    }

    /**
     * <p>Returns a single fight object, specified by a String email.</p>
     *
     * <p>If there is more than one fight with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the fight to be returned
     * @return The first fight with the specified email
     */
    Flight findByFlightNumber(String flightNumber) {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_BY_FLIGHTNUMBER, Flight.class).setParameter("flightNumber", flightNumber); 
        return query.getSingleResult();
    }

    /**
     * <p>Returns a single fight object, specified by a String flightNumber.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param flightNumber The flightNumber field of the fight to be returned
     * @return The first fight with the specified flightNumber
     */
    Flight findByFlightDeparture(String flightDeparture) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> criteria = cb.createQuery(Flight.class);
        Root<Flight> flight = criteria.from(Flight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(fight).where(CB.equal(fight.get(fight_.flightNumber), flightNumber));
        criteria.select(flight).where(cb.equal(flight.get("flightDeparture"), flightDeparture));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Returns a single fight object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param lastName The lastName field of the fight to be returned
     * @return The first fight with the specified lastName
     */
    Flight findByFlightDestination(String flightDestination) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> criteria = cb.createQuery(Flight.class);
        Root<Flight> flight = criteria.from(Flight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(flight).where(CB.equal(flight.get(Flight_.flightDestination), flightDestination));
        criteria.select(flight).where(cb.equal(flight.get("flightDestination"), flightDestination));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Persists the provided fight object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param fight The fight object to be persisted
     * @return The fight object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight create(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightRepository.create() - Creating" + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        // Write the fight to the database.
        em.persist(flight);
        
        return flight;
    }

    /**
     * <p>Updates an existing fight object in the application database with the provided fight object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param fight The fight object to be merged with an existing fight
     * @return The fight that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight update(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightRepository.update() - Updating " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        // Either update the fight or add it if it can't be found.
        em.merge(flight);
        
        return flight;
    }

    /**
     * <p>Deletes the provided fight object from the application database if found there</p>
     *
     * @param fight The fight object to be removed from the application database
     * @return The fight object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Flight delete(Flight flight) throws Exception {
        log.info("FlightRepository.delete() - Deleting " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        if (flight.getId() != null) {
            
        	em.remove(em.merge(flight));
            
        } else {
            log.info("FlightRepository.delete() - No ID was found so can't Delete.");
        }
        
        return flight;
    }

}
