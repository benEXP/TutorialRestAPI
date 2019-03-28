$(document).ready(function () {

    $("#createButton").click(function() {
        createEmployee();
    });

    $("#deleteButton").click(function() {
        deleteEmployee();
    });

    $("#updateButton").click(function() {
        updateEmployee()
    });

    $("#clearButton").click(function() {
        resetInput();
    });

    $("#cancelButton").click(function() {
        window.location='../../index.html';
    });
});

function createEmployee() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/api/employees/new",
        headers: {
            'Token' : 'default'
        },
        data: JSON.stringify({
            id: $("#id_txt").val(),
            name: $("#name_txt").val(),
            age: $("#age_txt").val(),
            role: $("#role_txt").val(),
        }),
        success: function(res) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "green")
            $('#textContainer').text(res);
        },
        error: function(e) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "red")
            $('#textContainer').text(e.responseText);
            console.log("ERROR : ", e.responseText);
        }
    });
}

function deleteEmployee() {
    var id_num = $("#id_txt").val();

    $.ajax({
        type: "DELETE",
        contentType: "application/json",
        url: "/api/employees/delete/"+id_num,
        headers: {
            'Token' : 'default'
        },

        success: function(res) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "green")
            $('#textContainer').text(res);
        },
        error: function(e) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "red")
            $('#textContainer').text(e.responseText);
            console.log("ERROR : ", e.responseText);
        }
    });
}

function updateEmployee() {
    var id_num = $("#id_txt").val()

    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: "/api/employees/update/"+id_num,
        headers: {
            'Token' : 'default'
        },
        data: JSON.stringify({
            id: $("#id_txt").val(),
            name: $("#name_txt").val(),
            age: $("#age_txt").val(),
            role: $("#role_txt").val(),
        }),
        success: function(res) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "green")
            $('#textContainer').text(res);
        },
        error: function(e) {
            $('#resultContainer').show();
            $("#textContainer").css("color", "red")
            $('#textContainer').text(e.responseText);
            console.log("ERROR : ", e.responseText);
        }
    });
}

function resetInput() {
    $("#id_txt").val("");
    $("#name_txt").val("");
    $("#age_txt").val("");
    $("#role_txt").val("");
}