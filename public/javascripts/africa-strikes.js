/**
 * Created by Igor on 2/14/2017.
 */
$(document).ready(function() {
    // Prevents the default behaviour of the "Enter" key from happening
    $(document).on("keypress", ":input:not(textarea)", function (event) {
        return event.keyCode != 13;
    });

    // Prevents the form being submitted twice!
    $("form").submit(function() {
        $(this).submit(function() {
            return false;
        });
        return true;
    });

    // Appends an option to the given select module
    window.appendToSelect = function(inner, value, selector) {
        var el = document.createElement('option');
        el.innerHTML = inner;
        el.value = value;
        selector.appendChild(el);
    };
});