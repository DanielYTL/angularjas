package org.jboss.quickstarts.wfk.flight;

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
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRepository;
import org.jboss.quickstarts.wfk.flight.FlightRESTService;
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
 * Flight creation functionality
 * (see {@link FlightRESTService#createFlight(Flight) createFlight(Flight)}).<p/>
 *
 * 
 * @author Yutong Liu
 * @see FlightRESTService
 */
@RunWith(Arquillian.class)
public class FlightRegistrationTest {

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
        //HttpComponents and org.JSON are required by FlightService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Flight.class, 
                        FlightRESTService.class, 
                        FlightRepository.class, 
                        FlightValidator.class, 
                        FlightService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    FlightRESTService flightRESTService;
    
    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Flight flight = createFlightInstance("YT888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New flight was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Flight flight = createFlightInstance("", "", "");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight register attempt failed with return code " + response.getStatus());
    }

	@SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateFlightNumber() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "LHR");
        flightRESTService.createFlight(flight);

        // Register a different user with the same flight number
        Flight anotherFlight = createFlightInstance("YT888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(anotherFlight);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate flight register attempt failed with return code " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testFlightNumber() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YTL88", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void testFlightNumber2() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YTLD8", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(6)
    public void testFlightNumber3() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YTLDH", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(7)
    public void testFlightNumber4() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("Y8888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
  
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testFlightNumber5() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("88888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(9)
    public void testFlightNumber6() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("a8888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(10)
    public void testFlightNumber7() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("aa888", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(11)
    public void testFlightNumber8() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("aaa88", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(12)
    public void testFlightNumber9() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("aaaa8", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(13)
    public void testFlightNumber10() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("aaaaa", "HKG", "LHR");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(14)
    public void testFlightSpe() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("!@£$%", "^&*", "()?");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(15)
    public void testFlightDapDes() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "HKG");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(16)
    public void testFlightDapDes2() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "hkg", "ncl");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(17)
    public void testFlightDapDes3() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "ncl");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(17)
    public void testFlightDapDes4() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "hkg", "NCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(18)
    public void testFlightDapDes5() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "hkG", "NCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(19)
    public void testFlightDapDes6() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "nCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(20)
    public void testFlightDapDes7() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKg", "nCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(21)
    public void testFlightDapDes8() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "NC?");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(22)
    public void testFlightDapDes9() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HKG", "Nc?");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(23)
    public void testFlightDapDes10() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HK?", "NCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(24)
    public void testFlightDapDes11() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "hK£", "NCL");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(25)
    public void testFlightDapDes12() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "hK£", "Nc^");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(26)
    public void testFlightDapDes13() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "HK£", "NC^");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(27)
    public void testFlightDapDes14() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "£$£", "£$£");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	@SuppressWarnings("unchecked")
    @Test
    @InSequence(28)
    public void testFlightDapDes15() throws Exception {
        // Register an initial user
        Flight flight = createFlightInstance("YT888", "@£$", "<>?");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Flight number only 2 upper cases and 3 number " + response.getStatus());
    }
	/**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.flight.Flight Flight} object for use in
     * testing. This object is not persisted.</p>
     * @param flightNumber The flight number of the Flight being created
     * @param flightDeparture  The flight departure of the Flight being created
     * @param flightDestination The flight destination of the Flight being created
     * @return The Flight object create
     */
	private Flight createFlightInstance(String flightNumber, String flightDeparture, String flightDestination) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setFlightDeparture(flightDeparture);
        flight.setFlightDestination(flightDestination);
        return flight;
    }
}
