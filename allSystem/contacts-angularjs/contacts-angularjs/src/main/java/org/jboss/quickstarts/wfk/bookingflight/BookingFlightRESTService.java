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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

/**
 * <p>This class exposes the functionality of {@link BookingFlightService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the BookingFlight resource is rest/bookingFlights .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Yutong Liu
 * @see BookingFlightService
 * @see javax.ws.rs.core.Response
 */
/*
 * The Path annotation defines this as a REST Web Service using JAX-RS.
 * 
 * By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they 
 * can be overridden by adding the Consumes or Produces annotations to the individual method.
 * 
 * It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow 
 * transaction demarcation when accessing the database." - Antonio Goncalves
 * 
 */
@Path("/bookingFlights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingFlightRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingFlightService service;
    
    /**
     * <p>Search for and return all the BookingFlights.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of BookingFlights
     */
    @GET
    public Response retrieveAllBookingFlights() {
        List<BookingFlight> bookingFlights = service.findAllOrderedByName();
        return Response.ok(bookingFlights).build();
    }

    /**
     * <p>Search for and return a BookingFlight identified by customer address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between customer addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate customer addresses.</p>
     *
     *
     * @param customer The string parameter value provided as a BookingFlight's customer
     * @return A Response containing a single BookingFlight
     */
    @GET
    @Path("/customerID/{id:[0-9]+}")
    public Response retrieveBookingFlightByCustomerID(@PathParam("id") long id) {
       // List<Book> book = service.findAllByCustomerId(id);
        BookingFlight bookingFlight = service.findAllByCustomerID(id);

        if (bookingFlight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Contact = " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getBookingFlightDate() + " " 
            + bookingFlight.getId());
        
        return Response.ok(bookingFlight).build();
    }
    
    @GET
    @Path("/flightID/{id:[0-9]+}")
    public Response retrieveBookingFlightByFlightID(@PathParam("id") long id) {
       // List<Book> book = service.findAllByFlightId(id);
        BookingFlight bookingFlight = service.findAllByFlightID(id);
        if (bookingFlight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Contact = " + bookingFlight.getCustomerID() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getBookingFlightDate() + " " 
            + bookingFlight.getId());
        
        return Response.ok(bookingFlight).build();
    }
    /**
     * <p>Search for and return a BookingFlight identified by id.</p>
     * 
     * @param id The long parameter value provided as a BookingFlight's id
     * @return A Response containing a single BookingFlight
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingFlightById(@PathParam("id") long id) {
        BookingFlight bookingFlight = service.findById(id);
        if (bookingFlight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found BookingFlight = " + bookingFlight.getFlightID() + " " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getId());
        
        return Response.ok(bookingFlight).build();
    }

    /**
     * <p>Creates a new bookingFlight from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingFlight The BookingFlight object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingFlightService#create(BookingFlight)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBookingFlight(BookingFlight bookingFlight) {
        log.info("createBookingFlight started. BookingFlight = " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getCustomerID() + " "  + bookingFlight.getId());
        if (bookingFlight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new BookingFlight.
            service.create(bookingFlight);

            // Create a "Resource Created" 201 Response and pass the bookingFlight back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(bookingFlight);
            
            log.info("createBookingFlight completed. BookingFlight = " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            //responseObj.put("bookingFlightDate", "That bookingFlightDate is already used, please use a unique bookingFlightDate");
            if(e.toString().contains("btc"))
                responseObj.put("btc", "That flightDate and/or customer are not  existed, Please check you information carefully");   
            if(e.toString().contains("date"))
               responseObj.put("bookingFlightDate", "That bookingFlightDate is  existed, Please check you information carefully");
                if(e.toString().contains("customerID"))
               responseObj.put("customerID", "That customerID is not existed, Please check you information carefully");
                 if(e.toString().contains("flightID"))
               responseObj.put("flightID", "That flightID is not existed, Please check you information carefully"); 
                 if(e.toString().contains("bookingFlight"))
                     responseObj.put("bookingFlight", "That flightID and/or date are existed, Please check you information carefully");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Updates a bookingFlight with the ID provided in the BookingFlight. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingFlight The BookingFlight object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingFlightService#update(BookingFlight)}
     * @param id The long parameter value provided as the id of the BookingFlight to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateBookingFlight(@PathParam("id") long id, BookingFlight bookingFlight) {
        if (bookingFlight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBookingFlight started. BookingFlight = " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getId());

        if (bookingFlight.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The bookingFlight ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(bookingFlight.getId()) == null) {
            // Verify if the bookingFlight exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the BookingFlight.
            service.update(bookingFlight);

            // Create an OK Response and pass the bookingFlight back in case it is needed.
            builder = Response.ok(bookingFlight);

            log.info("updateBookingFlight completed. BookingFlight = " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("flightID", "That flight ID is already used, please use a unique flight ID");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/BookingFlightRESTService.java line 242.");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Deletes a bookingFlight using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the BookingFlight to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBookingFlight(@PathParam("id") Long id) {
        log.info("deleteBookingFlight started. BookingFlight ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
            BookingFlight bookingFlight = service.findById(id);
            if (bookingFlight != null) {
                service.delete(bookingFlight);
            } else {
                log.info("BookingFlightRESTService - deleteBookingFlight - No bookingFlight with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBookingFlight completed. BookingFlight = " + bookingFlight.getBookingFlightDate() + " " + bookingFlight.getFlightID() + " " + bookingFlight.getCustomerID() + " " + bookingFlight.getId());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    /**
     * <p>Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can be used
     * by calling client applications to display violations to users.<p/>
     * 
     * @param violations A Set of violations that need to be reported in the Response body
     * @return A Bad Request (400) Response containing all violation messages
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
}
