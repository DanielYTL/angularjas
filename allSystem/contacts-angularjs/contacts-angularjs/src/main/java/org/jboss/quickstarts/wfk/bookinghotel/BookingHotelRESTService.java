package org.jboss.quickstarts.wfk.bookinghotel;

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
@Path("/bookingHotels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingHotelRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingHotelService service;
    
    /**
     * <p>Search for and return all the BookingHotels.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of BookingHotels
     */
    @GET
    public Response retrieveAllBookingHotels() {
        List<BookingHotel> bookingHotels = service.findAllOrderedById();
        return Response.ok(bookingHotels).build();
    }

    
    /**
     * <p>Search for and return a BookingHotel identified by id.</p>
     * 
     * @param id The long parameter value provided as a BookingHotel's id
     * @return A Response containing a single BookingHotel
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingHotelById(@PathParam("id") long id) {
        BookingHotel bookingHotel = service.findById(id);
        if (bookingHotel == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        
        return Response.ok(bookingHotel).build();
    }
    
    @GET
    @Path("/contacts/{id:[0-9]+}")
    public Response retrieveBookingHotelByCustomer(@PathParam("id") long id) {
        List<BookingHotel> bookingHotels = service.findByCustomerId(id);
        if (bookingHotels == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
      //  log.info("findByCustomer " + customerId + ": found BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        
        return Response.ok(bookingHotels).build();
    }
    
    @GET
    @Path("/hotels/{id:[0-9]+}")
    public Response retrieveBookingHotelByHotel(@PathParam("id") long id) {
        List<BookingHotel> bookingHotels = service.findByHotelId(id);
        if (bookingHotels == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
      //  log.info("findByHotel " + hotelId + ": found BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        
        return Response.ok(bookingHotels).build();
    }

    /**
     * <p>Creates a new bookingHotel from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingHotel The BookingHotel object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingHotelService#create(BookingHotel)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBookingHotel(BookingHotel bookingHotel) {
        log.info("createBookingHotel started. BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        if (bookingHotel == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new BookingHotel.
            service.create(bookingHotel);

            // Create a "Resource Created" 201 Response and pass the bookingHotel back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(bookingHotel);
            
            log.info("createBookingHotel completed. BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            //responseObj.put("bookingHotelDate", "That hotel is already booked that day, please use book another day");
            //builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("cnh"))
                responseObj.put("cnh", "That hotel and customer are not existed");  
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("customer"))
               responseObj.put("customer", "That customer is not existed");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("hotel"))
               responseObj.put("hotel", "That hotel is not existed"); 
               builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            if(e.toString().contains("bookingHotel"))
                responseObj.put("bookingHotel", "That hotel is already booked that day");
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
     * <p>Updates a bookingHotel with the ID provided in the BookingHotel. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param bookingHotel The BookingHotel object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingHotelService#update(BookingHotel)}
     * @param id The long parameter value provided as the id of the BookingHotel to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateBookingHotel(@PathParam("id") long id, BookingHotel bookingHotel) {
        if (bookingHotel == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBookingHotel started. BookingHotel = " +  bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());

        if (bookingHotel.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The bookingHotel ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(bookingHotel.getId()) == null) {
            // Verify if the contact exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the BookingHotel.
            service.update(bookingHotel);
            

            // Create an OK Response and pass the bookingHotel back in case it is needed.
            builder = Response.ok(bookingHotel);

            log.info("updateBookingHotel completed. BookingHotel = " + bookingHotel.getId()+ " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/ContactRESTService.java line 242.");
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
     * <p>Deletes a bookingHotel using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the BookingHotel to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteBookingHotel(@PathParam("id") Long id) {
        log.info("deleteBookingHotel started. BookingHotel ID = " + id);
        Response.ResponseBuilder builder = null;

        Map<String, String> responseObj = new HashMap<String, String>();
        try {
        	BookingHotel bookingHotel = service.findById(id);
            if (bookingHotel != null) {
                service.delete(bookingHotel);
            } else {
                log.info("BookingHotelRESTService - deleteBookingHotel - No bookingHotel with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBookingHotel completed. BookingHotel = " + bookingHotel.getId() + " " + bookingHotel.getCustomer()+ " " + bookingHotel.getHotel() + " " + bookingHotel.getBookingHotelDate());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
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