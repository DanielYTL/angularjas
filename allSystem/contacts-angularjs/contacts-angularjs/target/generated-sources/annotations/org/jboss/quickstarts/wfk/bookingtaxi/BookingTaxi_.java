package org.jboss.quickstarts.wfk.bookingtaxi;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.taxi.Taxi;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BookingTaxi.class)
public abstract class BookingTaxi_ {

	public static volatile SingularAttribute<BookingTaxi, Taxi> taxiid;
	public static volatile SingularAttribute<BookingTaxi, Long> id;
	public static volatile SingularAttribute<BookingTaxi, Date> taxidate;
	public static volatile SingularAttribute<BookingTaxi, Contact> customer;

}

