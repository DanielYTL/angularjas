package org.jboss.quickstarts.wfk.bookinghotel;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.Hotel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BookingHotel.class)
public abstract class BookingHotel_ {

	public static volatile SingularAttribute<BookingHotel, Contact> contact;
	public static volatile SingularAttribute<BookingHotel, Hotel> hotel;
	public static volatile SingularAttribute<BookingHotel, Long> id;
	public static volatile SingularAttribute<BookingHotel, Date> bookingHotelDate;

}

