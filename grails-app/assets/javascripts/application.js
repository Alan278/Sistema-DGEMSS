// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-3.5.1
//= require popper-1.16.1
//= require bootstrap-4.5.2
//= require custom-modal
//= require stomp
//= require sockjs
//= require_self

(function() {
    'use strict';
    window.addEventListener('load', function() {
        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        var forms = document.getElementsByClassName('needs-validation');
        // Loop over them and prevent submission
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();

                    var modalDiv = $("<div class='modal fade' tabindex='-1' role='dialog' aria-labelledby='myLargeModalLabel' aria-hidden='true'/>")
                    var modalDialog = $("<div class='modal-dialog modal-md'/>");
                    var modalContent = $("<div class='modal-content'/>");
                    var modalHeader = $("<div class='modal-header'/>");
                    var modalTitle = $("<h5 class='modal-title'>Atención</h5>");
                    var modalClose = $("<button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                    var modalBody = $("<div class='modal-body'/>");
                    var modalFooter = $("<div class='modal-footer'/>");
                    var modalOk = $("<button type='button' class='btn btn-primary' data-dismiss='modal'>Aceptar</button>");

                    $(modalDiv).append(modalDialog);
                    $(modalDialog).append(modalContent);
                    $(modalContent).append(modalHeader);
                    $(modalHeader).append(modalTitle);
                    $(modalHeader).append(modalClose);
                    $(modalContent).append(modalBody);
                    $(modalBody).append("Por favor complete todos los campos marcados con color rojo.");
                    $(modalContent).append(modalFooter);
                    $(modalFooter).append(modalOk);

                    $(modalDiv).modal("show");
                } else {
                    if ($(event.target).hasClass("confirm")) {
                        confirmModal(event);
                    } else {
                        loadingModal();
                    }
                }

                form.classList.add('was-validated');
            }, false);
        });
    }, false);

    $(".custom-file-input").on("change", function() {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });

})();
