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
import org.jboss.quickstarts.wfk.taxi.Taxi;


/**
 * <p>This class exposes the functionality of {@link BookingTaxiService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the BookingTaxi resource is rest/bookings .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Joshua Wilson
 * @see BookingTaxiService
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
@Path("/bookingtaxis")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingTaxiRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingTaxiService service;
    
    /**
     * <p>Search for and return all the BookingTaxis.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of BookingTaxis
     */
    @GET
    public Response retrieveAllBookingTaxis() {
        List<BookingTaxi> bookingTaxis = service.findAllOrderedByName();
        return Response.ok(bookingTaxis).build();
    }

    /**
     * <p>Search for and return a BookingTaxi identified by customer address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between customer addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate customer addresses.</p>
     *
     *
     * @param customer The string parameter value provided as a BookingTaxi's customer
     * @return A Response containing a single BookingTaxi
     */
    @GET
    @Path("/customer/{id:[0-9]+}")
    public Response retrieveBookingTaxiByCustomerId(@PathParam("id") long id) {
       // List<Book> book = service.findAllByCustomerId(id);
        BookingTaxi bookingTaxi = service.findAllByCustomer(id);

        if (bookingTaxi == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
      //  log.info("findById " + id + ": found Contact = " + book.getCustomer() + " " + book.getFlight() + " " + book.getBookingDate() + " " 
        //    + book.getId());
        
        return Response.ok(bookingTaxi).build();
    }
    
    @GET
    @Path("/taxiid/{id:[0-9]+}")
    public Response retrieveBookingTaxiByTaxiId(@PathParam("id") long id) {
       // List<Book> book = service.findAllByFlightId(id);
        BookingTaxi bookingTaxi = service.findAllByTaxiid(id);
        if (bookingTaxi == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
      //  log.info("findById " + id + ": found Contact = " + book.getCustomer() + " " + book.getFlight() + " " + book.getBookingDate() + " " 
        //    + book.getId());
        
        return Response.ok(bookingTaxi).build();
    }
    
    /**
     * <p>Search for and return a BookingTaxi identified by id.</p>
     * 
     * @param id The long parameter value provided as a BookingTaxi's id
     * @return A Response containing a single BookingTaxi
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingTaxiById(@PathParam("id") long id) {
        BookingTaxi bookingTaxi = service.findById(id);
        if (bookingTaxi == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found BookingTaxi = " + bookingTaxi.getTaxiid()+ " " + bookingTaxi.getTaxidate() + " " + bookingTaxi.getCustomer()  
               + " " + bookingTaxi.getId());
        
        return Response.ok(bookingTaxi).build();
    }

    /**
     * <p>Creates a new bookingTaxi from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingTaxi The BookingTaxi object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingTaxiService#create(BookingTaxi)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBookingTaxi(BookingTaxi bookingTaxi) {
        log.info("createBookingTaxi started. BookingTaxi = " + bookingTaxi.getTaxiid()  + " " + bookingTaxi.getTaxidate()+ " " + bookingTaxi.getCustomer()+ " " + bookingTaxi.getId());
        if (bookingTaxi == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new BookingTaxi.
            service.create(bookingTaxi);

            // Create a "Resource Created" 201 Response and pass the bookingTaxi back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(bookingTaxi);
            
            log.info("createBookingTaxi completed. BookingTaxi = " + bookingTaxi.getTaxiid() + " " + bookingTaxi.getTaxidate()+ " " + bookingTaxi.getCustomer()  
                + " " + bookingTaxi.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
           //if(e.toString().contains("date"))
            //responseObj.put("taxidate", "That taxi id is already used, please use a unique taxi id");
            //if(e.toString().contains("customer")&&e.toString().contains("taxiid"))
              //  responseObj.put("customer", "That customer and taxid are not existed");
            if(e.toString().contains("btc"))
                responseObj.put("btc", "That taxidate and customer are not  existed");   
            if(e.toString().contains("date"))
               responseObj.put("taxidate", "That taxidate is  existed");
                if(e.toString().contains("customer"))
               responseObj.put("customer", "That customer is not existed");
                 if(e.toString().contains("taxiid"))
               responseObj.put("taxiid", "That taxiid is not existed"); 
                 if(e.toString().contains("bookingTaxi"))
                     responseObj.put("bookingTaxi", "That taxiid and date are existed");
      
           
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
     * <p>Updates a bookingTaxi with the ID provided in the BookingTaxi. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingTaxi The BookingTaxi object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingTaxiService#update(BookingTaxi)}
     * @param id The long parameter value provided as the id of the BookingTaxi to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateBookingTaxi(@PathParam("id") long id, BookingTaxi bookingTaxi) {
        if (bookingTaxi == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBookingTaxi started. BookingTaxi = " + bookingTaxi.getTaxiid()  + " " + bookingTaxi.getTaxidate()+ " " + bookingTaxi.getCustomer()
                 + " " + bookingTaxi.getId());

        if (bookingTaxi.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The bookingTaxi ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(bookingTaxi.getId()) == null) {
            // Verify if the bookingTaxi exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the BookingTaxi.
            service.update(bookingTaxi);

            // Create an OK Response and pass the bookingTaxi back in case it is needed.
            builder = Response.ok(bookingTaxi);

            log.info("updateBookingTaxi completed. BookingTaxi = " + bookingTaxi.getTaxiid()  + " " + bookingTaxi.getTaxidate()+ " " + bookingTaxi.getCustomer()+ " " + bookingTaxi.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("taxiid", "That taxi id is already used, please use a unique taxi id");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/BookingTaxiRESTService.java line 242.");
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
     * <p>Deletes a bookingTaxi using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the BookingTaxi to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBookingTaxi(@PathParam("id") Long id) {
        log.info("deleteBookingTaxi started. BookingTaxi ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
            BookingTaxi bookingTaxi = service.findById(id);
            if (bookingTaxi != null) {
                service.delete(bookingTaxi);
            } else {
                log.info("BookingTaxiRESTService - deleteBookingTaxi - No bookingTaxi with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBookingTaxi completed. BookingTaxi = " + bookingTaxi.getTaxiid()  + " " + bookingTaxi.getTaxidate() + " " + bookingTaxi.getCustomer()+ " " + bookingTaxi.getId());
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
