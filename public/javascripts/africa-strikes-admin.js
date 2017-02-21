/**
 * Created by Igor on 2/14/2017.
 */

$(document).ready(function() {

    $('#refreshListButton').on('click', function () {
        // unselect the select button
        deselectSelection();
        // clear search field
        $('#searchStrikeAdmin').val("");
        // clear the list
        for(var i = document.getElementById("strikeSelectAdmin").options.length - 1; i >= 0; i--){
            document.getElementById("strikeSelectAdmin").remove(i);
        }
        // fill the list
        $.getJSON('/admin/strikes', function (updatedStrikes) {
            for(var i = 0; i < updatedStrikes.length; i++){
                appendToSelect(updatedStrikes[i], updatedStrikes[i], document.getElementById("strikeSelectAdmin"));
            }
        });
    });

    $('#searchStrikeAdmin').keyup(function (event) {
        if ( event.which == 13 ) { // Needs to select the given number or the first in list
            if(this.value !== "")
                searchStrike(this.value, undefined);
            else
                event.preventDefault();
        }
        // To uncheck the selected checkbox
        deselectSelection();
        // Search the strike
        searchStrike(this.value, undefined);
    });

    // Deselects the selections for checked, unchecked and all
    window.deselectSelection = function(){
        var ele = document.getElementsByName("checkedOrUncheckedStrikes");
        for(var i=0;i<ele.length;i++)
            ele[i].checked = false;
    };

    $("input[name='checkedOrUncheckedStrikes']").change(function (e) {
        $('#searchStrikeAdmin').val("");
        $.getJSON('/admin/type/' + $(this).val(), function (strikesReturned) {
            searchStrike(-1, strikesReturned);
        });
    });
});