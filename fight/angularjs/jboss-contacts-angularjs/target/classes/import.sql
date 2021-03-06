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
insert into Contact (id, first_name, last_name, email, phone_number, birth_date, state) values (10001, 'John', 'Smith', 'john.smith@mailinator.com', '(075) 555-12122', '1963-06-03', 'NY')
insert into Contact (id, first_name, last_name, email, phone_number, birth_date, state) values (10002, 'David', 'Young', 'david.young@gmail.com', '(075) 555-12123', '1963-06-03', 'NY')
insert into Contact (id, first_name, last_name, email, phone_number, birth_date, state) values (10003, 'Yutong', 'Liu', 'yutong.liu@durham.ac.uk', '(075) 888-88888', '1996-08-07', 'NY')

insert into Flight (id, flight_number, flight_departure, flight_destination) values (20101, 'YT888', 'HKG', 'LHR')
insert into Flight (id, flight_number, flight_departure, flight_destination) values (20102, 'LY666', 'NCL', 'AMS')
insert into Flight (id, flight_number, flight_departure, flight_destination) values (20103, 'LT999', 'LHR', 'NCL')

insert into Booking (id, customer_ID, flight_ID, booking_Date) values (30101, 10001, 20101, '2015-07-03')
insert into Booking (id, customer_ID, flight_ID, booking_Date) values (30102, 10002, 20102, '2015-08-03')
insert into Booking (id, customer_ID, flight_ID, booking_Date) values (30103, 10003, 20103, '2015-09-03')