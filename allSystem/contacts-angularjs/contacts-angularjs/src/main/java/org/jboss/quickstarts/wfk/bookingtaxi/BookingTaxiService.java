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


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 * @author Joshua Wilson
 * @see BookingTaxiValidator
 * @see BookingTaxiRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class BookingTaxiService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingTaxiValidator validator;

    @Inject
    private BookingTaxiRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link BookingTaxi} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of BookingTaxi objects
     */
    List<BookingTaxi> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }
    BookingTaxi findAllByCustomer(Long customer) {
        return crud.findByCustomer(customer);
    }
    BookingTaxi findAllByTaxiid(Long taxiid) {
        return crud.findByTaxiid(taxiid);
    }
    BookingTaxi findAllByTaxidate(Date taxidate) {
        return crud.findByTaxidate(taxidate);
    }

    /**
     * <p>Returns a single BookingTaxi object, specified by a Long id.<p/>
     * 
     * @param id The id field of the BookingTaxi to be returned
     * @return The BookingTaxi with the specified id
     */
    BookingTaxi findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single BookingTaxi object, specified by a String customer.</p>
     *
     * <p>If there is more than one BookingTaxi with the specified customer, only the first encountered will be returned.<p/>
     * 
     * @param customer The customer field of the BookingTaxi to be returned
     * @return The first BookingTaxi with the specified customer
     */

    /**
     * <p>Returns a single BookingTaxi object, specified by a String taxiid.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param taxiid The taxiid field of the BookingTaxi to be returned
     * @return The first BookingTaxi with the specified taxiid
     */


    
    /**
     * <p>Returns a single BookingTaxi object, specified by a String taxiid.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param taxiid The taxiid field of the BookingTaxi to be returned
     * @return The first BookingTaxi with the specified taxiid
     */
  
    
    /**
     * <p>Writes the provided BookingTaxi object to the application database.<p/>
     *
     * <p>Validates the data in the provided BookingTaxi object using a {@link BookingTaxiValidator} object.<p/>
     * 
     * @param bookingTaxi The BookingTaxi object to be written to the database using a {@link BookingTaxiRepository} object
     * @return The BookingTaxi object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingTaxi create(BookingTaxi bookingTaxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingTaxiService.create() - Creating " + bookingTaxi.getCustomer()+" "+ bookingTaxi.getTaxiid());
        
        // Check to make sure the data fits with the parameters in the BookingTaxi model and passes validation.
        validator.validateBookingTaxi(bookingTaxi);

        //Perform a rest call to get the state of the bookingTaxi from the allareacodes.com API
      /** URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", bookingTaxi.getPhoneNumber().substring(1,4))
                .setParameter("tracking_customer", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        bookingTaxi.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);
*/
//bookingTaxi.setState("unknown");
        // Write the bookingTaxi to the database.
        return crud.create(bookingTaxi);
    }

    /**
     * <p>Updates an existing BookingTaxi object in the application database with the provided BookingTaxi object.<p/>
     *
     * <p>Validates the data in the provided BookingTaxi object using a BookingTaxiValidator object.<p/>
     * 
     * @param bookingTaxi The BookingTaxi object to be passed as an update to the application database
     * @return The BookingTaxi object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingTaxi update(BookingTaxi bookingTaxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingTaxiService.update() - Updating " + bookingTaxi.getCustomer()+" "+ bookingTaxi.getTaxiid() );
        
        // Check to make sure the data fits with the parameters in the BookingTaxi model and passes validation.
        validator.validateBookingTaxi(bookingTaxi);

        //Perform a rest call to get the state of the bookingTaxi from the allareacodes.com API
       /** URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", bookingTaxi.getPhoneNumber().substring(1,4))
                .setParameter("tracking_customer", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        bookingTaxi.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);
*/
        // Either update the bookingTaxi or add it if it can't be found.
        return crud.update(bookingTaxi);
    }

    /**
     * <p>Deletes the provided BookingTaxi object from the application database if found there.<p/>
     * 
     * @param bookingTaxi The BookingTaxi object to be removed from the application database
     * @return The BookingTaxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingTaxi delete(BookingTaxi bookingTaxi) throws Exception {
        log.info("BookingTaxiService.delete() - Deleting " + bookingTaxi.getCustomer()+" "+ bookingTaxi.getTaxiid() );
        
        BookingTaxi deletedBookingTaxi = null;
        
        if (bookingTaxi.getId() != null) {
            deletedBookingTaxi = crud.delete(bookingTaxi);
        } else {
            log.info("BookingTaxiService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedBookingTaxi;
    }

}
