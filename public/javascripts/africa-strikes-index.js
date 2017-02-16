/**
 * Created by Igor on 2/1/2017.
 */
$(document).ready(function(){

    $('#duration').val(0);

    $("#articleUploadButton").change(function () {
        $("#uploadedArticleOutput").append(document.getElementById("articleUploadButton").value);
    });

});