(function() {
    'use strict';
    angular
        .module('app.booking')
        .factory('Booking', Booking);

    Booking.$inject = ['$resource'];

    function Booking($resource) {
        //Declares Contact as a class of Resource, whose instance.$methods and Class.methods may be used
        // to easily interact with the RESTful rest/contacts endpoint via $http.
        var Contact = $resource(
            'rest/bookings/:contactId',
            {contactId: '@id'},
            {
                'update': {method: 'PUT'}
            }
        );
        //Declare public class variable to act as a pseudo-cache TODO: use proper $cacheFactor cache in Contact
        Contact.data = [];
        return Contact;
    }
})();