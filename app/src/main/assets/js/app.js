//angular.module('AddressBookApp', []).controller('MainCtrl', function ($scope) {
//  $scope.contacts = [];
//  $scope.search = '';
//
//  $scope.submitContact = function () {
//    const contact = {
//      customerID: document.getElementById('customerID').value,
//      companyName: document.getElementById('companyName').value,
//      contactName: document.getElementById('contactName').value.trim(),
//      contactTitle: document.getElementById('contactTitle').value,
//      address: document.getElementById('address').value,
//      city: document.getElementById('city').value,
//      email: document.getElementById('email').value.trim(),
//      postalCode: document.getElementById('postalCode').value,
//      country: document.getElementById('country').value,
//      phone: document.getElementById('phone').value,
//      fax: document.getElementById('fax').value
//    };
//    if (!contact.contactName || !contact.email) {
//      alert('Name and Email are required.');
//      return;
//    }
//    const json = JSON.stringify(contact);
//    if (window.Android && window.Android.createNewContact) {
//      window.Android.createNewContact(json);
//    } else {
//      console.log("Android interface not available.");
//    }
//  }
//
//  $scope.newContact = function () {
//    const name = prompt("Name:");
//    const phone = prompt("Phone:");
//    const email = prompt("Email:");
//    if (name && phone && email) {
//      $scope.contacts.push({ name, phone, email });
//    }
//  };
//
//  $scope.deleteContact = function (index) {
//    $scope.contacts.splice(index, 1);
//  };
//
//  $scope.editContact = function (index) {
//    const contact = $scope.contacts[index];
//    const name = prompt("Name:", contact.name);
//    const phone = prompt("Phone:", contact.phone);
//    const email = prompt("Email:", contact.email);
//    if (name && phone && email) {
//      $scope.contacts[index] = { name, phone, email };
//    }
//  };
//
//  $scope.importXmlFile = function (event) {
//    console.log('File selected:', event.target.files[0]);
//    const file = event.target.files[0];
//    if (file) {
//      const reader = new FileReader();
//      reader.onload = function (e) {
//        const xml = e.target.result;
//        const parser = new DOMParser();
//        const xmlDoc = parser.parseFromString(xml, "text/xml");
//        const contacts = xmlDoc.getElementsByTagName("Contact");
//
//        const contactsList = Array.from(contacts).map(contact => ({
//          customerID: contact.getElementsByTagName("CustomerID")[0]?.textContent || "",
//          companyName: contact.getElementsByTagName("CompanyName")[0]?.textContent || "",
//          contactName: contact.getElementsByTagName("ContactName")[0]?.textContent || "",
//          contactTitle: contact.getElementsByTagName("ContactTitle")[0]?.textContent || "",
//          address: contact.getElementsByTagName("Address")[0]?.textContent || "",
//          city: contact.getElementsByTagName("City")[0]?.textContent || "",
//          email: contact.getElementsByTagName("Email")[0]?.textContent || "",
//          postalCode: contact.getElementsByTagName("PostalCode")[0]?.textContent || "",
//          country: contact.getElementsByTagName("Country")[0]?.textContent || "",
//          phone: contact.getElementsByTagName("Phone")[0]?.textContent || "",
//          fax: contact.getElementsByTagName("Fax")[0]?.textContent || ""
//        }));
//
//        // Convert contacts to JSON and send to Android
//        const jsonContacts = JSON.stringify(contactsList);
//        window.Android?.importContacts?.(jsonContacts);
//        $scope.$apply();
//      };
//      reader.readAsText(file);
//    }
//  };
//
//});


function submitContact() {
    const contact = {
      customerID: document.getElementById('customerID').value,
      companyName: document.getElementById('companyName').value,
      contactName: document.getElementById('contactName').value.trim(),
      contactTitle: document.getElementById('contactTitle').value,
      address: document.getElementById('address').value,
      city: document.getElementById('city').value,
      email: document.getElementById('email').value.trim(),
      postalCode: document.getElementById('postalCode').value,
      country: document.getElementById('country').value,
      phone: document.getElementById('phone').value,
      fax: document.getElementById('fax').value
    };
    if (!contact.contactName || !contact.email) {
      alert('Name and Email are required.');
      return;
    }
    const json = JSON.stringify(contact);
    if (window.Android && window.Android.createNewContact) {
      window.Android.createNewContact(json);
    } else {
      console.log("Android interface not available.");
    }
  }

  function loadContacts(jsonContacts) {
      console.log('Loading contacts:', jsonContacts);
      try {
          const contacts = JSON.parse(jsonContacts);
          const scope = angular.element(document.body).scope();
          scope.contacts = contacts;
          scope.$apply();
      } catch (error) {
          console.error('Error loading contacts:', error);
      }
  }