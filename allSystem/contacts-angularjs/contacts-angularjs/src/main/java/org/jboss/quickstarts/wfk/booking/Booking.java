package org.jboss.quickstarts.wfk.booking;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.Hotel;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.taxi.Taxi;

/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b ORDER BY b.id ASC"),
    @NamedQuery(name = Booking.FIND_BY_DATE, query = "SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate"),
    @NamedQuery(name = Booking.FIND_BY_HOTEL, query = "SELECT b FROM Booking b WHERE b.hotel.id = :hotelId"),
    @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.contact.id = :customerId")
})
@XmlRootElement
@Table(name = "Booking", uniqueConstraints = @UniqueConstraint(columnNames = "id"))

public class Booking implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_DATE = "Booking.findByDate";
    public static final String FIND_BY_HOTEL = "Booking.findByHotel";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_TAXI = "Booking.findByTaxi";
    public static final String FIND_BY_FLIGHT = "Booking.findByFlight";
    
    @ManyToOne
    @JoinColumn(name = "hotelId")
    private Hotel hotel;
    
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Contact contact;
    
    @ManyToOne
       @JoinColumn(name = "taxi_id")
       private Taxi taxiid;
    
    @ManyToOne
    @JoinColumn(name = "flight_ID")
    private Flight flightID;
    
    /*
     * The  error messages match the ones in the UI so that the user isn't confused by two similar error messages for
     * the same error after hitting submit. This is if the form submits while having validation errors. The only
     * difference is that there are no periods(.) at the end of these message sentences, this gives us a way to verify
     * where the message came from.
     * 
     * Each variable name exactly matches the ones used on the HTML form name attribute so that when an error for that
     * variable occurs it can be sent to the correct input field on the form.  
     */

    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    @NotNull
    @Future(message = "Bookingdates can not be in the past. Please choose one from the future")
    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
   public void setCustomer(Contact contact){
   	 this.contact = contact;
   }
   public Contact getCustomer() {
       return contact;
   }
   
   public Hotel getHotel() {
       return hotel;
   }
   public void setHotel(Hotel hotel) {
       this.hotel = hotel;
   }
   public Taxi getTaxiid() {
       return taxiid;
   }
   public void setTaxiid(Taxi taxiid) {
       this.taxiid=taxiid;
   }
   
   public Flight getFlightID() {
       return flightID;
   }

   public void setFlightID(Flight flightID) {
       this.flightID = flightID;
   }
   
    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }


}