
 
package org.jboss.quickstarts.wfk.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.Booking;
import org.jboss.quickstarts.wfk.booking.BookingRepository;
import org.jboss.quickstarts.wfk.booking.BookingRESTService;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.booking.BookingValidator;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.ContactRESTService;
import org.jboss.quickstarts.wfk.contact.ContactRepository;
import org.jboss.quickstarts.wfk.contact.ContactService;
import org.jboss.quickstarts.wfk.contact.ContactValidator;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRESTService;
import org.jboss.quickstarts.wfk.flight.FlightRepository;
import org.jboss.quickstarts.wfk.flight.FlightService;
import org.jboss.quickstarts.wfk.flight.FlightValidator;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Booking creation functionality
 * (see {@link BookingRESTService#createBooking(Booking) createBooking(Booking)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see BookingRESTService
 */
@RunWith(Arquillian.class)
public class BookingRegistrationTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        //HttpComponents and org.JSON are required by BookingService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Contact.class,
            			ContactRESTService.class, 
            			ContactRepository.class, 
            			ContactValidator.class, 
            			ContactService.class, 
            			Flight.class, 
            			FlightRESTService.class, 
            			FlightRepository.class, 
            			FlightValidator.class, 
            			FlightService.class,
            			Booking.class, 
                        BookingRESTService.class, 
                        BookingRepository.class, 
                        BookingValidator.class, 
                        BookingService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    BookingRESTService bookingRESTService;
    @Inject
    ContactRESTService contactRESTService;
    @Inject
    ContactService contactService;
    @Inject
    FlightRESTService flightRESTService;
    @Inject
    FlightService flightService;
    @Inject
    BookingService bookingService;
    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    @SuppressWarnings("deprecation")
	private Date date = new Date(2015,10,9);
    private Contact contact = new Contact();
    private Flight flight = new Flight();
    
    /*Contact contact = createContactInstance("John", "Smith", "john.smith@mailinator.com", "(075) 555-12122", date);
    Contact contact1 = createContactInstance("David", "Young", "david.young@gmail.com", "(075) 555-12123", date);
    Contact contact2 = createContactInstance("Yutong", "Liu", "yutong.liu@durham.ac.uk", "(075) 888-88888", date);
    Flight flight = createFlightInstance("YT888", "HKG", "LHR");
    Flight flight1 = createFlightInstance("LY666", "NCL", "AMS");
    Flight flight2 = createFlightInstance("LT999", "LHR", "NCL");*/

    /*@Test
    @InSequence(1)
    public void testRegister() throws Exception {
        //need to fix
    	Contact contact = createContactInstance("John", "Smith", "john.smith@mailinator.com", "(075) 555-12122", date);        
    	Response response1 = contactRESTService.createContact(contact);
        
    	Flight flight = createFlightInstance("YT888", "HKG", "LHR");
    	Response response2 = flightRESTService.createFlight(flight);
    	
        Booking booking=createBookingById(contact.getId(),flight.getId(),date);
        Response response3 = bookingRESTService.createBooking(booking);
        //CORRECT  
        assertEquals("Unexpected response status", 201, response1.getStatus());
        log.info(" New contact was persisted and returned status " + response1.getStatus());
        assertEquals("Unexpected response status", 201, response2.getStatus());
        log.info(" New hotel was persisted and returned status " + response2.getStatus());
        assertEquals("Unexpected response status", 201, response3.getStatus());
        log.info(" New booking was persisted and returned status " + response3.getStatus());
    }*/
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
    	Booking booking = createBookingInstance(contact, flight, date);
        Response response = bookingRESTService.createBooking(booking);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid hotel register attempt failed with return code " + response.getStatus());
    }
    
    /*@SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
//need to fix    
    public void testDeleteBooking() throws Exception {
    	Contact contact = createContactInstance("John", "Smith", "john.smith@mailinator.com", "(075) 555-12122", date);        
    	Response response1 = contactRESTService.createContact(contact);
        assertEquals("Unexpected response status", 201, response1.getStatus());
        log.info(" New contact was persisted and returned status " + response1.getStatus());
        
        Flight flight = createFlightInstance("YT888", "HKG", "LHR");
        Response response2 = flightRESTService.createFlight(flight);
        assertEquals("Unexpected response status", 201, response2.getStatus());
        log.info(" New hotel was persisted and returned status " + response2.getStatus());
        
    	Booking booking = createBookingInstance(contact, flight, date);
    	Response response3 = bookingRESTService.createBooking(booking);
    	assertEquals("Unexpected response status", 201, response3.getStatus());
        log.info("Booking record was persisted and returned status " + response3.getStatus());
        
        Response response5 = bookingRESTService.deleteBooking(booking.getId());

        assertEquals("Unexpected response status", 204, response5.getStatus());
        log.info("Booking record was deleted and returned status " + response5.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testDuplicateDate() throws Exception {
    	Contact contact = createContactInstance("John", "Smith", "john.smith@mailinator.com", "(075) 555-12122", date);        
        Response response1 = contactRESTService.createContact(contact);
        assertEquals("Unexpected response status", 201, response1.getStatus());
        log.info(" New customer was persisted and returned status " + response1.getStatus());
        
        Flight flight = createFlightInstance("YT888", "HKG", "LHR");
        Response response2 = flightRESTService.createFlight(flight);
        assertEquals("Unexpected response status", 201, response2.getStatus());
        log.info(" New hotel was persisted and returned status " + response2.getStatus());
        
    	Booking booking = createBookingById(contact.getId(),flight.getId(),date);
        bookingRESTService.createBooking(booking);

        Contact anotherContact = createContactInstance("John", "Smith", "john.smith@mailinator.com", "(075) 555-12122", date);
        Response response3 = contactRESTService.createContact(anotherContact);
        assertEquals("Unexpected response status", 201, response3.getStatus());
        log.info(" New contact was persisted and returned status " + response3.getStatus());
        
    	Booking anotherBooking = createBookingById(anotherContact.getId(),flight.getId(),date);
        Response response4 = bookingRESTService.createBooking(anotherBooking);
        
        assertEquals("Unexpected response status", 409, response4.getStatus());
        assertNotNull("response.getEntity() should not be null", response4.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response4.getEntity(), 1,
            ((Map<String, String>) response4.getEntity()).size());
        log.info("Duplicate contact register attempt failed with return code " + response4.getStatus());
    }*/
    
    @Test
    @InSequence(5)
    public void testRetrieveAllBookings() throws Exception {
        Response response = bookingRESTService.retrieveAllBookings();

        assertEquals("Unexpected response status", 200, response.getStatus());
        log.info(" All bookings retrieve failed with return code " + response.getStatus());
    }
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.booking.Booking Booking} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param firstName The first name of the Booking being created
     * @param lastName  The last name of the Booking being created
     * @param email     The email address of the Booking being created
     * @param phone     The phone number of the Booking being created
     * @param birthDate The birth date of the Booking being created
     * @return The Booking object create
     */
    private Contact createContactInstance(String firstName, String lastName, String email, String phone, Date birthDate) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phone);
        contact.setBirthDate(birthDate);
        return contact;
    }
    private Flight createFlightInstance(String flightNumber, String flightDeparture, String flightDestination) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setFlightDeparture(flightDeparture);
        flight.setFlightDestination(flightDestination);
        return flight;
    }
    private Booking createBookingInstance(Contact contact, Flight flight, Date bookingDate){
		Booking booking = new Booking();
		booking.setFlightID(flight);
		booking.setCustomerID(contact);
		booking.setBookingDate(bookingDate);
    	return booking;
    	
    }
    private Booking createBookingById(Long flightID, Long contactID, Date bookingDate) {
        Booking booking = new Booking();
        Flight flight = flightService.findById(flightID);
        Contact contact = contactService.findById(contactID);
        booking.setCustomerID(contact);
        booking.setFlightID(flight);
        booking.setBookingDate(bookingDate);
       
        return booking;
    }
}
