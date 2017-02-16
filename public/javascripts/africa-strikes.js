/**
 * Created by Igor on 2/14/2017.
 */
$(document).ready(function() {
    $(document).on("keypress", ":input:not(textarea)", function (event) {
        return event.keyCode != 13;
    });

    function appendToSelect(inner, value, selector) {
        var el = document.createElement('option');
        el.innerHTML = inner;
        el.value = value;
        selector.appendChild(el);
    }

    $(function(){
        $('#sectors').selectize();
        $('#hiscoOccupations').selectize();
        $('#causeOfDisputes').selectize();
        $('#identityElements').selectize();
        $('#strikeDefinitions').selectize();
        $('#companyNames').selectize({
            plugins: ['remove_button'],
            create: function(input) {
                return {
                    value: input,
                    text: input
                }
            }
        });
        $('#occupations').selectize({
            plugins: ['remove_button'],
            create:function(input){
                return{
                    value: input,
                    text: input
                }
            }
        });
        $('#identityDetails').selectize({
            plugins: ['remove_button'],
            create:function(input){
                return{
                    value: input,
                    text: input
                }
            }
        });
    });

    function updateArticleDayField() {
        var yearArticleElement = document.getElementById("yearOfArticle");
        var monthArticleElement = document.getElementById("monthOfArticle");
        var yearNum = yearArticleElement.options[yearArticleElement.selectedIndex].value;
        var monthNum = monthArticleElement.selectedIndex -1; // Due to the months containing a default value
        $('#dayOfArticle').empty();
        for(var i = 0; i <= getNumberOfDays(yearNum, monthNum); i++) {
            appendToSelect(i, i, document.getElementById("dayOfArticle"));
        }
    }

    function updateStrikeDayField() {
        var yearStartElement = document.getElementById("yearStart");
        var monthStartElement = document.getElementById("monthStart");
        var yearNum = yearStartElement.options[yearStartElement.selectedIndex].value;
        var monthNum = monthStartElement.selectedIndex -1; // Due to the months containing a default value
        $('#dayStart').empty();
        for(var i = 0; i <= getNumberOfDays(yearNum, monthNum); i++) {
            appendToSelect(i, i, document.getElementById("dayStart"));
        }
    }

    function updateStrikeDayEndField(){
        var yearEndElement = document.getElementById("yearEnd");
        var monthEndElement = document.getElementById("monthEnd");
        var yearNum = yearEndElement.options[yearEndElement.selectedIndex].value;
        var monthNum = monthEndElement.selectedIndex -1; // Due to the months containing a default value
        $('#dayEnd').empty();
        for(var i = 0; i <= getNumberOfDays(yearNum, monthNum); i++) {
            appendToSelect(i, i, document.getElementById("dayEnd"));
        }
    }

    function getNumberOfDays(yearNumber, monthNumber) {
        var isLeap = ((yearNumber % 4) == 0 && ((yearNumber % 100) != 0 || (yearNumber % 400) == 0));
        return [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][(monthNumber === -1)? 0 : monthNumber];
    }

    $("#yearOfArticle").change(function () {
        updateArticleDayField();
    });

    $("#monthOfArticle").change(function () {
        updateArticleDayField();
    });

    $("#yearStart").change(function () {
        updateStrikeDayField();
    });

    $("#monthStart").change(function () {
        updateStrikeDayField();
    });

    $('#yearEnd').change(function () {
        updateStrikeDayEndField();
    });

    $('#monthEnd').change(function () {
        updateStrikeDayEndField();
    });

});