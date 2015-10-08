package org.jboss.quickstarts.wfk.flight;

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
 * @see FlightValidator
 * @see FlightRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class FlightService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private FlightValidator validator;

    @Inject
    private FlightRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Flight} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of Flight objects
     */
    List<Flight> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Flight object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Flight to be returned
     * @return The Flight with the specified id
     */
    public Flight findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Flight object, specified by a String email.</p>
     *
     * <p>If there is more than one Flight with the specified email, only the first encountered will be returned.<p/>
     * 
     * @param email The email field of the Flight to be returned
     * @return The first Flight with the specified email
     */
    Flight findByFlightnumber(String flightNumber) {
        return crud.findByFlightNumber(flightNumber);
    }

    /**
     * <p>Returns a single Flight object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param firstName The firstName field of the Flight to be returned
     * @return The first Flight with the specified firstName
     */
    Flight findByFlightDeparture(String flightDeparture) {
        return crud.findByFlightDeparture(flightDeparture);
    }

    /**
     * <p>Returns a single Flight object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param lastName The lastName field of the Flight to be returned
     * @return The first Flight with the specified lastName
     */
    Flight findByFlightDestination(String flightDestination) {
        return crud.findByFlightDestination(flightDestination);
    }

    /**
     * <p>Writes the provided Flight object to the application database.<p/>
     *
     * <p>Validates the data in the provided Flight object using a {@link FlightValidator} object.<p/>
     * 
     * @param Flight The Flight object to be written to the database using a {@link FlightRepository} object
     * @return The Flight object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight create(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightService.create() - Creating " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        // Check to make sure the data fits with the parameters in the Flight model and passes validation.
        validator.validateFlight(flight);

        //Perform a rest call to get the state of the Flight from the allareacodes.com API
      /**  URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", Flight.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        Flight.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);*/

       // flight.setState("unknown");
        // Write the Flight to the database.
        return crud.create(flight);
    }

    /**
     * <p>Updates an existing Flight object in the application database with the provided Flight object.<p/>
     *
     * <p>Validates the data in the provided Flight object using a FlightValidator object.<p/>
     * 
     * @param Flight The Flight object to be passed as an update to the application database
     * @return The Flight object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Flight update(Flight flight) throws ConstraintViolationException, ValidationException, Exception {
        log.info("FlightService.update() - Updating " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        // Check to make sure the data fits with the parameters in the Flight model and passes validation.
        validator.validateFlight(flight);

        //Perform a rest call to get the state of the Flight from the allareacodes.com API
       /** URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", Flight.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        Flight.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);*/

        // Either update the Flight or add it if it can't be found.
        return crud.update(flight);
    }

    /**
     * <p>Deletes the provided Flight object from the application database if found there.<p/>
     * 
     * @param Flight The Flight object to be removed from the application database
     * @return The Flight object that has been successfully removed from the application database; or null
     * @throws Exception
     */
   Flight delete(Flight flight) throws Exception {
        log.info("FlightService.delete() - Deleting " + flight.getFlightDeparture() + " " + flight.getFlightDestination());
        
        Flight deletedFlight = null;
        
        if (flight.getId() != null) {
            deletedFlight = crud.delete(flight);
        } else {
            log.info("FlightService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedFlight;
    }

}
