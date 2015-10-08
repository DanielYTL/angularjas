--
-- JBoss, Home of Professional Open Source
-- Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- Since the database doesn't know to increase the Sequence to match what is manually loaded here it starts at 1 and tries
--  to enter a record with the same PK and create an error.  If we use a high we don't interfere with the sequencing (at least until later).
-- NOTE: this file should be removed for production systems. 
insert into Contact (id, first_name, last_name, email, phone_number) values (10001, 'John', 'Smith', 'john.smith@mailinator.com', '(012) 5555-1212')
insert into Contact (id, first_name, last_name, email, phone_number) values (10002, 'Davey', 'Jones', 'davey.jones@locker.com', '(012) 5555-3333',)
insert into Hotel (id, hotel_name, phone_number, postcode) values (20001, 'Hilton', '(077) 5432-1234', 'ABCCBA')
insert into Hotel (id, hotel_name, phone_number, postcode) values (20002, 'Collingwood', '(077) 9999-9999', 'DH13LT')
insert into Taxi (id, registration, seat) values (30001,'sdge001','2')
insert into Flight (id, flight_number, flight_departure, flight_destination) values (40001, 'YT888', 'HKG', 'LHR')
insert into Flight (id, flight_number, flight_departure, flight_destination) values (40002, 'LY666', 'NCL', 'AMS')
insert into Booking (id, customerId, hotelId, taxi_id, flight_ID, booking_date) values (60001, 10001, 20002, 30001, 40001 '2015-08-07')
insert into Booking (id, customerId, hotelId, taxi_id, flight_ID , booking_date) values (60002, 10002, 20001, 30001, 40002 '2015-09-21')