$(document).ready(function() {

    // Refreshes the users in the list
    $('#refreshListButton').on('click', function () {
        // clear search field
        $('#searchUserAdmin').val("");
        // clear the list
        for (var i = document.getElementById("userSelectAdmin").options.length - 1; i >= 0; i--) {
            document.getElementById("userSelectAdmin").remove(i);
        }
        // fill the list
        $.getJSON('/admin/user-overview/users', function (updatedUsers) {
            for (var i = 0; i < updatedUsers.length; i++) {
                appendToSelect(updatedUsers[i].username, updatedUsers[i].id, document.getElementById("userSelectAdmin"));
            }
            // clear the fields
            for (var key in updatedUsers[0]) {
                if (updatedUsers[0].hasOwnProperty(key)) {
                    $('[id=' + key + ']').val("");
                }
            }
        });
    });

    $('#searchUserAdmin').keyup(function (event) {
        if (event.which == 13) { // Needs to select the given number or the first in list
            if (this.value !== "")
                searchUser(this.value);
            else
                event.preventDefault();
        }
        // Search the strike
        searchUser(this.value);
    });

    $('#userSelectAdmin').change(function () {
        var selectedUser = $('#userSelectAdmin').val().toString();
        console.log(selectedUser);
        $.getJSON('/admin/user-overview/user/' + selectedUser, function(user){
            console.log(user);

            for (var key in user) {
                if (user.hasOwnProperty(key)) {
                    $('[id=' + key + ']').val(user[key]);
                }
            }

        });
    });

});