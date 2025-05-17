console.log('app.js loaded successfully');
var app = angular.module('addressBook', []);
var testName3 = "Test Name 3";

app.run(function() {
    console.log('AngularJS application is running');
});

app.controller('ContactController', function($scope) {
  console.log('ContactController initialized');
    $scope.contacts = [];
    $scope.newContact = {};
    $scope.testName = "AngularJS Test";
    

    
    if (window.Android) {
        console.log("Android bridge is available");
        Android.testBridge("Bridge test from Angular");
    } else {
        console.log("Android bridge is not available");
    }

    //Function to be called from WebAppBridge when contacts are updated
    window.loadContacts = function(jsonContacts) {
        // Parse the JSON string to array of contacts
        const contacts = JSON.parse(jsonContacts);
        // Update the scope with new contacts
        $scope.contacts = contacts;
        // Force Angular to update the view
        $scope.$apply();
    };

    $scope.addContact = function() {
        if ($scope.newContact.contactName && $scope.newContact.email) {
            // Send new contact to Android
            Android.createNewContact(JSON.stringify($scope.newContact));
            // Clear the form
            $scope.newContact = {};
        }
    };

    $scope.deleteContact = function(index) {
        Android.deleteContact(index);
    };

    $scope.updateContact = function(contact) {
        Android.updateContact(JSON.stringify(contact));
    };

    //Test bridge connection
    Android.testBridge("Connected from Angular");
});