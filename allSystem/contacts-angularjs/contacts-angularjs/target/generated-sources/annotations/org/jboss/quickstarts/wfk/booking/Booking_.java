package org.jboss.quickstarts.wfk.booking;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.contact.Hotel;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.taxi.Taxi;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Booking.class)
public abstract class Booking_ {

	public static volatile SingularAttribute<Booking, Contact> contact;
	public static volatile SingularAttribute<Booking, Taxi> taxiid;
	public static volatile SingularAttribute<Booking, Hotel> hotel;
	public static volatile SingularAttribute<Booking, Flight> flightID;
	public static volatile SingularAttribute<Booking, Date> bookingDate;
	public static volatile SingularAttribute<Booking, Long> id;

}

