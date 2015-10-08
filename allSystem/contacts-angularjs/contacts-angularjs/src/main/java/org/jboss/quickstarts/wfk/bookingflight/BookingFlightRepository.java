/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.bookingflight;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingFlightService} with the
 * Domain/Entity Object (see {@link BookingFlight}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Yutong Liu
 * @see BookingFlight
 * @see javax.persistence.EntityManager
 */
public class BookingFlightRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link BookingFlight} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of BookingFlight objects
     */
    List<BookingFlight> findAllOrderedByName() {
        TypedQuery<BookingFlight> query = em.createNamedQuery(BookingFlight.FIND_ALL, BookingFlight.class); 
        return query.getResultList();
    }
    BookingFlight findByBookingFlightDate(Date bookingFlightDate) {
        TypedQuery<BookingFlight> query = em.createNamedQuery(BookingFlight.FIND_BY_BOOKINGDATE, BookingFlight.class).setParameter("bookingFlight_Date", bookingFlightDate); 
        return query.getSingleResult();
    }
    /**
     * <p>Returns a single Contact object, specified by a Long id.<p/>
     *
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    BookingFlight findById(Long id) {
        return em.find(BookingFlight.class, id);
    }

    BookingFlight findByCustomerID(Long customerID) {
        TypedQuery<BookingFlight> query = em.createNamedQuery(BookingFlight.FIND_BY_CUSTOMERID, BookingFlight.class).setParameter("customer_ID", customerID); 
        return query.getSingleResult();
    }
    BookingFlight findByFlightID(Long flightID) {
        TypedQuery<BookingFlight> query = em.createNamedQuery(BookingFlight.FIND_BY_FLIGHTID, BookingFlight.class).setParameter("flight_ID", flightID); 
        return query.getSingleResult();
    }
	List<BookingFlight> findListByFlightID(Long flightID) {
		TypedQuery<BookingFlight> query = em.createNamedQuery(
				BookingFlight.FIND_BY_FLIGHTID, BookingFlight.class).setParameter("flight_ID", flightID);
		return query.getResultList();
	}
    
    /**
     * <p>Returns a single Contact object, specified by a String bookingFlightDate.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param bookingFlightDate The bookingFlightDate field of the Contact to be returned
     * @return The first Contact with the specified bookingFlightDate
     */
/*    BookingFlight findByCustomerID(Long customerID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BookingFlight> criteria = cb.createQuery(BookingFlight.class);
        Root<BookingFlight> bookingFlight = criteria.from(BookingFlight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(bookingFlight).where(cb.equal(bookingFlight.get(Contact_.bookingFlightDate), bookingFlightDate));
        criteria.select(bookingFlight).where(cb.equal(bookingFlight.get("customerID"), customerID));
        return em.createQuery(criteria).getSingleResult();
    }*/

    /**
     * <p>Returns a single Contact object, specified by a String flightID.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param flightID The flightID field of the Contact to be returned
     * @return The first Contact with the specified flightID
     */
 /*   BookingFlight findByFlightID(Long flightID) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BookingFlight> criteria = cb.createQuery(BookingFlight.class);
        Root<BookingFlight> bookingFlight = criteria.from(BookingFlight.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(bookingFlight).where(cb.equal(bookingFlight.get(Contact_.flightID), flightID));
        criteria.select(bookingFlight).where(cb.equal(bookingFlight.get("flightID"), flightID));
        return em.createQuery(criteria).getSingleResult();
    }*/

    /**
     * <p>Persists the provided Contact object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param bookingFlight The Contact object to be persisted
     * @return The Contact object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingFlight create(BookingFlight bookingFlight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingFlightRepository.create() - Creating " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        // Write the bookingFlight to the database.
        em.persist(bookingFlight);
        
        return bookingFlight;
    }

    /**
     * <p>Updates an existing Contact object in the application database with the provided Contact object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param bookingFlight The Contact object to be merged with an existing Contact
     * @return The Contact that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingFlight update(BookingFlight bookingFlight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingFlightRepository.update() - Updating " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        // Either update the bookingFlight or add it if it can't be found.
        em.merge(bookingFlight);
        
        return bookingFlight;
    }

    /**
     * <p>Deletes the provided Contact object from the application database if found there</p>
     *
     * @param bookingFlight The Contact object to be removed from the application database
     * @return The Contact object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingFlight delete(BookingFlight bookingFlight) throws Exception {
        log.info("BookingFlightRepository.delete() - Deleting " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        if (bookingFlight.getId() != null) {
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
            em.remove(em.merge(bookingFlight));
            
        } else {
            log.info("BookingFlightRepository.delete() - No ID was found so can't Delete.");
        }
        
        return bookingFlight;
    }

}
