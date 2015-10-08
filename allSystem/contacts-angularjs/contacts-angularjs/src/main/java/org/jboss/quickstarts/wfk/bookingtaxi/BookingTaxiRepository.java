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
package org.jboss.quickstarts.wfk.bookingtaxi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.taxi.Taxi;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Joshua Wilson
 * @see Booking
 * @see javax.persistence.EntityManager
 */
public class BookingTaxiRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link BookingTaxi} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of BookingTaxi objects
     */
    List<BookingTaxi> findAllOrderedByName() {
        TypedQuery<BookingTaxi> query = em.createNamedQuery(BookingTaxi.FIND_ALL, BookingTaxi.class); 
        return query.getResultList();
    }
    BookingTaxi findByTaxidate(Date taxidate) {
    	 TypedQuery<BookingTaxi> query = em.createNamedQuery(BookingTaxi.FIND_BY_DATE, BookingTaxi.class).setParameter("taxidate", taxidate); 
         return query.getSingleResult();
    }
    /**
     * <p>Returns a single BookingTaxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the BookingTaxi to be returned
     * @return The BookingTaxi with the specified id
     */
    BookingTaxi findById(Long id) {
        return em.find(BookingTaxi.class, id);
    }
    
    BookingTaxi findByCustomer(Long customerId) {
        TypedQuery<BookingTaxi> query = em.createNamedQuery(BookingTaxi.FIND_BY_CUSTOMER, BookingTaxi.class).setParameter("customer_id", customerId); 
        return query.getSingleResult();
    }
    BookingTaxi findByTaxiid(Long taxiid) {
        TypedQuery<BookingTaxi> query = em.createNamedQuery(BookingTaxi.FIND_BY_TAXIID, BookingTaxi.class).setParameter("taxi_id", taxiid); 
        return query.getSingleResult();
    }
	List<BookingTaxi> findListByTaxiid(Long taxiid) {
		TypedQuery<BookingTaxi> query = em.createNamedQuery(
				BookingTaxi.FIND_BY_TAXIID, BookingTaxi.class).setParameter("taxi_id", taxiid);
		return query.getResultList();
	}

    /**
     * <p>Returns a single BookingTaxi object, specified by a String email.</p>
     *
     * <p>If there is more than one BookingTaxi with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the BookingTaxi to be returned
     * @return The first BookingTaxi with the specified email
     */
  
    /**
     * <p>Persists the provided BookingTaxi object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param bookingTaxi The BookingTaxi object to be persisted
     * @return The BookingTaxi object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingTaxi create(BookingTaxi bookingTaxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingTaxiRepository.create() - Creating " + bookingTaxi.getCustomer() + bookingTaxi.getTaxiid() );
        
        // Write the bookingTaxi to the database.
        em.persist(bookingTaxi);
        
        return bookingTaxi;
    }
    
    

    /**
     * <p>Updates an existing BookingTaxi object in the application database with the provided BookingTaxi object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param bookingTaxi The BookingTaxi object to be merged with an existing BookingTaxi
     * @return The BookingTaxi that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingTaxi update(BookingTaxi bookingTaxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingTaxiRepository.update() - Updating " + bookingTaxi.getCustomer() + bookingTaxi.getTaxiid());
        
        // Either update the bookingTaxi or add it if it can't be found.
        em.merge(bookingTaxi);
        
        return bookingTaxi;
    }

    
    /**
     * <p>Deletes the provided BookingTaxi object from the application database if found there</p>
     *
     * @param bookingTaxi The BookingTaxi object to be removed from the application database
     * @return The BookingTaxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingTaxi delete(BookingTaxi bookingTaxi) throws Exception {
        log.info("BookingTaxiRepository.delete() - Deleting " + bookingTaxi.getCustomer() + bookingTaxi.getTaxiid());
        
        if (bookingTaxi.getId() != null) {
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
            em.remove(em.merge(bookingTaxi));
            
        } else {
            log.info("BookingTaxiRepository.delete() - No ID was found so can't Delete.");
        }
        
        return bookingTaxi;
    }

    
    
}
