<button type="button" class="btn btn-outline-danger btn-sm" data-toggle="modal" data-target="#modalEliminacion_${registroId}" data-tooltip="tooltip" data-placement="top" title="Eliminar Registro">
    <i class= "fa fa-trash"></i>
</button>

<div class="modal fade" id="modalEliminacion_${registroId}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLongTitle">${titulo}</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                ${descripcion}
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                <a onclick="mostrarProceso();"  href="${url}/${registroId}" class="btn btn-primary">Aceptar</a>
            </div>
        </div>
    </div>
</div>