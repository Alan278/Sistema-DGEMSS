$('#instituciones').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#institucionId').val(li.val());
        $('#carreraId').val('');
        $('#institucionIdAux').val(li.val());
        $('#carreraIdAux').val('');
        $('#planEstudiosId').val('');
        $('#cicloEscolarId').val('');
        $('#form-busqueda').submit();
    }
);

$('#carreras').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#carreraId').val(li.val());
        $('#carreraIdAux').val(li.val());
        $('#planEstudiosId').val('');
        $('#cicloEscolarId').val('');
        $('#form-busqueda').submit();
    }
);

$('#planesEstudios').editableSelect()
    .on('select.editable-select', function (e, li) {
        $('#planEstudiosId').val(li.val());
    }
);

function mostrarProceso(time){
    time = time || 10000
    $("#procesando").modal("show");
    setTimeout(function(){
        $("#procesando").modal('hide');
    }, time);
}
