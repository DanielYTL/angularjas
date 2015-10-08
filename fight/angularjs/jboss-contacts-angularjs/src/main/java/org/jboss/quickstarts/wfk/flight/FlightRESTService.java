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
package org.jboss.quickstarts.wfk.flight;

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

/**
 * <p>This class exposes the functionality of {@link FlightService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Flight resource is rest/Flights .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Yutong Liu
 * @see FlightService
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
@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class FlightRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private FlightService service;
    
    /**
     * <p>Search for and return all the Flights.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Flights
     */
    @GET
    public Response retrieveAllFlights() {
        List<Flight> flights = service.findAllOrderedByName();
        return Response.ok(flights).build();
    }

    /**
     * <p>Search for and return a Flight identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Flight's email
     * @return A Response containing a single Flight
     */
    @GET  
    @Path("/{flight_number:^.+@.+$}")
    public Response retrieveFlightsByFlightnumber(@PathParam("flightNumber") String flightNumber) {
        Flight flight;
        try {
            flight = service.findByFlightnumber(flightNumber);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(flight).build();
    }
    
    
    /**
     * <p>Search for and return a Flight identified by id.</p>
     * 
     * @param id The long parameter value provided as a Flight's id
     * @return A Response containing a single Flight
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveFlightById(@PathParam("id") long id) {
        Flight flight = service.findById(id);
        if ( flight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
                 + flight.getId());
        
        return Response.ok(flight).build();
    }

    /**
     * <p>Creates a new Flight from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param Flight The Flight object, constructed automatically from JSON input, to be <i>created</i> via {@link FlightService#create(Flight)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createFlight(Flight flight) {
        log.info("createFlight started. Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
             + flight.getId());
        if (flight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Flight.
            service.create(flight);

            // Create a "Resource Created" 201 Response and pass the Flight back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(flight);
            
            log.info("createFlight completed. Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
                + flight.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            if(e.toString().contains("flightNumber")){
            	responseObj.put("flight_number", "That flight number is already used, please use a unique flight number");
        	}
            if(e.toString().contains("Destination")){
            	responseObj.put("flightDestination", "please use a unique flight flightDestination");
        	}
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
     * <p>Updates a Flight with the ID provided in the Flight. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param Flight The Flight object, constructed automatically from JSON input, to be <i>updated</i> via {@link FlightService#update(Flight)}
     * @param id The long parameter value provided as the id of the Flight to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateFlight(@PathParam("id") long id, Flight flight) {
        if (flight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("findById " + id + "updateFlight started. Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
                + flight.getId());

        if (flight.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The flight ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(flight.getId()) == null) {
            // Verify if the Flight exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Flight.
            service.update(flight);

            // Create an OK Response and pass the Flight back in case it is needed.
            builder = Response.ok(flight);

            log.info("findById " + id + "updateFlight completed. Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
                + flight.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("flight_number", "That flight number is already used, please use a unique flight number");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/FlightRESTService.java line 242.");
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
     * <p>Deletes a Flight using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the Flight to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteFlight(@PathParam("id") Long id) {
        log.info("deleteFlight started. Flight ID = " + id);
        Response.ResponseBuilder builder = null;

        /*try {
            Flight flight = service.findById(id);
            if (flight != null) {
                service.delete(flight);
            } else {
                log.info("FlightRESTService - deleteFlight - No flight with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteFlight completed. Flight = " + flight.getFlightNumber() + " " + flight.getFlightDeparture() + " " + flight.getFlightDestination() + " " 
                + flight.getId());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
*/            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error",/* e.getMessage()*/"cannot delete");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        //}

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
