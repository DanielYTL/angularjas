package org.jboss.quickstarts.wfk.contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
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
@Path("/hotels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class HotelRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private HotelService service;
    
    /**
     * <p>Search for and return all the Hotels.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Hotels
     */
    @GET
    public Response retrieveAllHotels() {
        List<Hotel> hotels = service.findAllOrderedByName();
        return Response.ok(hotels).build();
    }
    
    /**
     * <p>Search for and return a Hotel identified by id.</p>
     * 
     * @param id The long parameter value provided as a Hotel's id
     * @return A Response containing a single Hotel
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveHotelById(@PathParam("id") long id) {
    	Hotel hotel = service.findById(id);
        if (hotel == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Hotel = " + hotel.getHotelName() + " " + hotel.getPhoneNumber() + " " + hotel.getPostcode() + " " + hotel.getId());
        
        return Response.ok(hotel).build();
    }

    /**
     * <p>Creates a new hotel from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param hotel The Hotel object, constructed automatically from JSON input, to be <i>created</i> via {@link HotelService#create(Hotel)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createHotel(Hotel hotel) {
        log.info("createHotel started. Hotel = " + hotel.getHotelName() + " " + hotel.getPhoneNumber() + " " + hotel.getPostcode() + " " + hotel.getId());
        if (hotel == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Hotel.
            service.create(hotel);

            // Create a "Resource Created" 201 Response and pass the hotel back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(hotel);
            
            log.info("createHotel completed. Hotel = " + hotel.getHotelName() + " " + hotel.getPhoneNumber() + " " + hotel.getPostcode() + " " + hotel.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("phoneNumber", "That phoneNumber is already used, please use a unique phoneNumber");
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
     * <p>Updates a hotel with the ID provided in the Hotel. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param hotel The Hotel object, constructed automatically from JSON input, to be <i>updated</i> via {@link HotelService#update(Hotel)}
     * @param id The long parameter value provided as the id of the Hotel to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateHotel(@PathParam("id") long id, Hotel hotel) {
        if (hotel == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateHotel started. Hotel = " + hotel.getHotelName() + " " + hotel.getPhoneNumber() + " " + hotel.getPostcode() + " " + hotel.getId());

        if (hotel.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The hotel ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(hotel.getId()) == null) {
            // Verify if the hotel exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Hotel.
            service.update(hotel);

            // Create an OK Response and pass the hotel back in case it is needed.
            builder = Response.ok(hotel);

            log.info("updateHotel completed. Hotel = " + hotel.getHotelName() + " " + hotel.getPhoneNumber() + " " + hotel.getPostcode() + " " + hotel.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/HotelRESTService.java line 186.");
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
    
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteHotel(@PathParam("id") Long id) {
        log.info("deleteHotel started. Hotel ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
            Hotel hotel = service.findById(id);
            if (hotel != null) {
                service.delete(hotel);
                Response response = Response.status(Response.Status.CONFLICT).entity("Hotel records cannot be deleted!").build();
                throw new WebApplicationException(response);
            } else {
                log.info("HotelRESTService - deleteHotel - No hotel with matching ID was found.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
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