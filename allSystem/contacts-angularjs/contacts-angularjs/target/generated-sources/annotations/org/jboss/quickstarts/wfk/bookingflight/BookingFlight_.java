package org.jboss.quickstarts.wfk.bookingflight;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.flight.Flight;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BookingFlight.class)
public abstract class BookingFlight_ {

	public static volatile SingularAttribute<BookingFlight, Contact> customerID;
	public static volatile SingularAttribute<BookingFlight, Date> bookingFlightDate;
	public static volatile SingularAttribute<BookingFlight, Flight> flightID;
	public static volatile SingularAttribute<BookingFlight, Long> id;

}

