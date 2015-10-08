package org.jboss.quickstarts.wfk.bookinghotel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.Hotel;

/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = BookingHotel.FIND_ALL, query = "SELECT b FROM BookingHotel b ORDER BY b.id ASC"),
    @NamedQuery(name = BookingHotel.FIND_BY_DATE, query = "SELECT b FROM BookingHotel b WHERE b.bookingHotelDate = :bookingHotelDate"),
    @NamedQuery(name = BookingHotel.FIND_BY_HOTEL, query = "SELECT b FROM BookingHotel b WHERE b.hotel.id = :hotelId"),
    @NamedQuery(name = BookingHotel.FIND_BY_CUSTOMER, query = "SELECT b FROM BookingHotel b WHERE b.contact.id = :customerId")
})
@XmlRootElement
@Table(name = "BookingHotel", uniqueConstraints = @UniqueConstraint(columnNames = "id"))

public class BookingHotel implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "BookingHotel.findAll";
    public static final String FIND_BY_DATE = "BookingHotel.findByDate";
    public static final String FIND_BY_HOTEL = "BookingHotel.findByHotel";
    public static final String FIND_BY_CUSTOMER = "BookingHotel.findByCustomer";
    
    @ManyToOne
    @JoinColumn(name = "hotelId")
    private Hotel hotel;
    
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Contact contact;
    
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
    @Future(message = "BookingHoteldates can not be in the past. Please choose one from the future")
    @Column(name = "bookingHotel_date")
    @Temporal(TemporalType.DATE)
    private Date bookingHotelDate;
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
    public Date getBookingHotelDate() {
        return bookingHotelDate;
    }

    public void setBookingHotelDate(Date bookingHotelDate) {
        this.bookingHotelDate = bookingHotelDate;
    }


}