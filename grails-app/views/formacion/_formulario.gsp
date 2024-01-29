<g:hiddenField name="planEstudiosId" value="${params.planEstudiosId ?: (formacion?.planEstudios?.id)}" />
<div class="form-row">
    <div class="form-group col-md-6">
        <label for="entidadNacimiento">Nombre<font color="red">*</font></label>
        <g:textField name="nombre" value="${params.nombre ?: (formacion?.nombre)}" class="form-control" required="true"/>
        <div class="invalid-feedback">
            Por favor ingrese el nombre.
        </div>
    </div>
</div>
<div class="form-row">
    <div class="form-group col-md-6">
        <label for="estatus">Tipo de formación <font color="red">*</font></label>
        <g:select class="form-control" id="tipoFormacionId" name="tipoFormacionId" from="${tiposFormaciones}" value="${params.tipoFormacionId ?: (formacion?.tipoFormacion?.id)}"  optionKey="id" optionValue="nombre" noSelection="${['':'Seleccione...']}" required="true"/>
        <div class="invalid-feedback">
            Por favor ingrese el estatus.
        </div>
    </div>
</div>
<div class="form-row">
    <div class="form-group col-md-3">
        <label for="entidadNacimiento">Requerido <font color="red">*</font></label>
        <g:checkBox name="requerida" value="${params.requerida ?: (formacion?.requerida)}" />
    </div>
    <div class="form-group col-md-3">
        <label for="entidadNacimiento">General <font color="red">*</font></label>
        <g:checkBox name="general" value="${params.general ?: (formacion?.general)}" />
    </div>
</div>
<div class="col-md-6 p-0">
    <div class="d-flex justify-content-end">
        <a href="/formacion/listar?planEstudiosId=${params.planEstudiosId ?: (formacion?.planEstudios?.id)}" class="btn btn-outline-danger">Cancelar</a>
        <button type="submit" class="btn btn-outline-primary ml-2">${btnMensaje}</button>
    </div>
</div>