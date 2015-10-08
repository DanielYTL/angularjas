/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function() {
    'use strict';
    angular
        .module('app.booking')
        .controller('BookingController', BookingController);

    BookingController.$inject = ['$scope', '$routeParams', '$location', 'Booking', 'messageBag'];

    function BookingController($scope, $routeParams, $location, Booking, messageBag) {
        //Assign Contact service to $scope variable
        $scope.bookings = Booking;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        //Get today's date for the birthDate form value max
        //$scope.date = Date.now();

        $scope.booking = {};
        $scope.create = true;

        //If $routeParams has :contactId then load the specified contact, and display edit controls on contactForm
        if($routeParams.hasOwnProperty('bookingId')) {
            $scope.booking = $scope.bookings.get({bookingId: $routeParams.bookingId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Contact object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.bookingForm) {
                $scope.bookingForm.$setPristine();
            }

            // Clear input fields. If $scope.contact was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.booking = {flightID: "", customerID: "", bookingDate: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addContact() function, which creates a new contact via the REST service,
        // using those details provided and displaying any error messages
        $scope.addBooking = function() {
            $scope.messages.clear();

            $scope.bookings.save($scope.booking,
                //Successful query
                function(data) {

                    // Update the list of contacts
                    $scope.bookings.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'Booking added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveContact() function, which saves the current contact using the REST service
        // and displays any error messages
        $scope.saveBooking = function() {
            $scope.messages.clear();
            $scope.booking.$update(
                //Successful query
                function(data) {
                    //Find the contact locally by id and update it
                    var idx = _.findIndex($scope.bookings.data, {'id': $scope.booking.id});
                    $scope.bookings.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'Booking saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };

        // Define a deleteContact() function, which saves the current contact using the REST service
        // and displays any error messages
        $scope.deleteBooking = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.booking.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the contact locally by id and remove it
                    var idx = _.findIndex($scope.bookings.data, {'id': $scope.booking.id});
                    $scope.bookings.data.splice(idx, 1);
                    //Mark success on the editContact form
                    $scope.messages.push('success', 'Booking removed');
                    //Redirect back to /home
                    $location.path('/booking');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };
    }
})();