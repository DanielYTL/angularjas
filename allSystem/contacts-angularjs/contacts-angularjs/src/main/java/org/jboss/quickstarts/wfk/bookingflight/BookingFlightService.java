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


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
 * @author Yutong Liu
 * @see BookingFlightValidator
 * @see BookingFlightRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class BookingFlightService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingFlightValidator bvalidator;

    @Inject
    private BookingFlightRepository bcrud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link BookingFlight} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of BookingFlight objects
     */
    List<BookingFlight> findAllOrderedByName() {
        return bcrud.findAllOrderedByName();
    }
    BookingFlight findAllByCustomerID(Long customerID) {
        return bcrud.findByCustomerID(customerID);
    }
    BookingFlight findAllByFlightID(Long flightID) {
        return bcrud.findByFlightID(flightID);
    }
    BookingFlight findAllByBookingFlightDate(Date bookingFlightDate) {
        return bcrud.findByBookingFlightDate(bookingFlightDate);
    }
    /**
     * <p>Returns a single BookingFlight object, specified by a Long id.<p/>
     * 
     * @param id The id field of the BookingFlight to be returned
     * @return The BookingFlight with the specified id
     */
    BookingFlight findById(Long id) {
        return bcrud.findById(id);
    }

    BookingFlight create(BookingFlight bookingFlight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingFlightService.create() - Creating " + bookingFlight.getId() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        // Check to make sure the data fits with the parameters in the BookingFlight model and passes validation.
        bvalidator.validateBookingFlight(bookingFlight);

        //Perform a rest call to get the state of the bookingFlight from the allareacodes.com API
       /* URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", bookingFlight.getPhoneNumber().substring(1,4))
                .setParameter("tracking_customerID", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        bookingFlight.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);*/
		//bookingFlight.setState("unknown");


        // Write the bookingFlight to the database.
        return bcrud.create(bookingFlight);
    }

    /**
     * <p>Updates an existing BookingFlight object in the application database with the provided BookingFlight object.<p/>
     *
     * <p>Validates the data in the provided BookingFlight object using a BookingFlightValidator object.<p/>
     * 
     * @param bookingFlight The BookingFlight object to be passed as an update to the application database
     * @return The BookingFlight object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    BookingFlight update(BookingFlight bookingFlight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingFlightService.update() - Updating " + bookingFlight.getId() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        // Check to make sure the data fits with the parameters in the BookingFlight model and passes validation.
        bvalidator.validateBookingFlight(bookingFlight);

        //Perform a rest call to get the state of the bookingFlight from the allareacodes.com API
       /* URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", bookingFlight.getPhoneNumber().substring(1,4))
                .setParameter("tracking_customerID", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        bookingFlight.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);*/

        // Either update the bookingFlight or add it if it can't be found.
        return bcrud.update(bookingFlight);
    }

    /**
     * <p>Deletes the provided BookingFlight object from the application database if found there.<p/>
     * 
     * @param bookingFlight The BookingFlight object to be removed from the application database
     * @return The BookingFlight object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    BookingFlight delete(BookingFlight bookingFlight) throws Exception {
        log.info("BookingFlightService.delete() - Deleting " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID());
        
        BookingFlight deletedBookingFlight = null;
        
        if (bookingFlight.getId() != null) {
            deletedBookingFlight = bcrud.delete(bookingFlight);
        } else {
            log.info("BookingFlightService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedBookingFlight;
    }

}
