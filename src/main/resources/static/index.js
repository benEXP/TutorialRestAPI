$(document).ready(function () {
        console.log("readyindex");
     $("#getAllButton").click(function() {
        getAllEmployees();
     });

     $("#createButton").click(function() {
        window.location='template/createEmployee/dashBoard.html';
     });

     $(".myCheckBox").on('click', function() {
       console.log("Column " + (index + 1) + " has value " + $(this).text());
     });
});

function getAllEmployees() {
    $('#table_id tbody > tr').remove();
    $.ajax({
        method: "GET",
        contentType: "application/json",
        url: "/api/employees/all",
        headers: {
            'Token' : 'default'
        },
        success: function(data) {

            $.each(data, function(key, value) {
               $('#table_id tbody').append("<tr><td><input class='myCheckBox' type= 'checkbox'></input></td><td>" + key + "</td><td>" + value.name + "</td><td>" + value.age + "</td><td>" + value.role + "</td></tr>");
            });

            console.log("SUCCESS: ", data);
        },
        error: function (e) {

            var json = "<h4>Ajax Response</h4><pre>"
                + e.responseText + "</pre>";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
        }
    });
}