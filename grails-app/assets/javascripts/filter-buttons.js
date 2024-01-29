// Acciones para los campos de filtrado de las vistas 'listar'

$('#estatusCertificado').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#estatusCertificadoId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#estatusConstancia').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#estatusConstanciaId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#estatusNotificacion').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#estatusNotificacionId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#estatusActa').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#estatusActaId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#anio').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#anio').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#mes').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#mes').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#instituciones').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#institucionId').val(li.val());
        $('#carreraId').val('');
        $('#planEstudiosId').val('');
        $('#cicloEscolarId').val('');
        $('#form-busqueda').submit();
    }
);

$('#carreras').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#carreraId').val(li.val());
        $('#planEstudiosId').val('');
        $('#cicloEscolarId').val('');
        $('#form-busqueda').submit();
    }
);

$('#planesEstudios').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#planEstudiosId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#ciclosEscolares').editableSelect()
    .on('select.editable-select', function (e, li) {
        mostrarProceso();
        $('#cicloEscolarId').val(li.val());
        $('#form-busqueda').submit();
    }
);

$('#limpiar-estatusCertificado').click(function() {
    mostrarProceso();
    $('#estatusCertificadoId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-estatusConstancia').click(function() {
    mostrarProceso();
    $('#estatusConstanciaId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-estatusNotificacion').click(function() {
    mostrarProceso();
    $('#estatusNotificacionId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-estatusActa').click(function() {
    mostrarProceso();
    $('#estatusActaId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-institucion').click(function() {
    mostrarProceso();
    $('#institucionId').val('');
    $('#carreraId').val('');
    $('#planEstudiosId').val('');
    $('#cicloEscolarId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-carrera').click(function() {
    mostrarProceso();
    $('#carreraId').val('');
    $('#planEstudiosId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-planEstudios').click(function() {
    mostrarProceso();
    $('#planEstudiosId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-cicloEscolar').click(function() {
    mostrarProceso();
    $('#cicloEscolarId').val('');
    $('#form-busqueda').submit();
});

$('#limpiar-search').click(function() {
    mostrarProceso();
    $('#search').val('');
    $('#form-busqueda').submit();
});

$('#buscar').click(function() {
    mostrarProceso();
    $('#form-busqueda').submit();
});

function mostrarProceso(time){
    time = time || 10000
    $("#procesando").modal("show");
    setTimeout(function(){
        $("#procesando").modal('hide');
    }, time);
}
