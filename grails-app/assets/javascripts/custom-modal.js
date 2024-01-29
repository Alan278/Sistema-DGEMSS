function loadingModal() {
    var modalDiv = $("<div class='modal fade' tabindex='-1' role='dialog' aria-labelledby='myLargeModalLabel' aria-hidden='true'/>")
    var modalDialog = $("<div class='modal-dialog modal-md'/>");
    var modalContent = $("<div class='modal-content'/>");
    var modalHeader = $("<div class='modal-header'/>");
    var modalTitle = $("<h5 class='modal-title'>Procesando...</h5>");
    var modalBody = $("<div class='modal-body'/>");
    var progress = $("<div class='progress'/>");
    var progressBar = $("<div class='progress-bar progress-bar-striped bg-info' role='progressbar' style='width: 100%' aria-valuenow='100' aria-valuemin='0' aria-valuemax='100'/>");

    $(modalDiv).append(modalDialog);
    $(modalDialog).append(modalContent);
    $(modalContent).append(modalHeader);
    $(modalHeader).append(modalTitle);
    $(modalContent).append(modalBody);
    $(progress).append(progressBar);
    $(modalBody).append(progress);

    $(modalDiv).modal("show");
}

function confirmModal(e) {
    e.preventDefault();
    var dialog = $("<div title='Confirmacion...' style='text-align: center;'><br/>¿Esta seguro de realizar esta accion?</div>");
    $(dialog).dialog({
        modal: true,
        width: 350,
        height: 180,
        buttons: {
            Cancelar: function() {
                $(this).dialog("close");
            },
            Continuar: function() {
                $(e.target).removeClass("confirm");
                $(this).dialog("close");

                if ($(e.target).is("a")) {
                    window.location = $(e.target).attr("href");
                } else if ($(e.target).is("form")) {
                    $(e.target).submit();
                }

                loadingDialog(e);

            }
        }
    });

    $(dialog).bind("dialogclose", function(event, ui) {
        $(".ui-dialog-content").dialog("close");
    });
}