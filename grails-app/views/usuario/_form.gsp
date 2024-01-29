<div class="row mb-4">
    <div class="col-md-12">
        <div class="card" >
            <div class="card-header">
                Datos del usuario
            </div>
            <div class="card-body py-4 px-4">
                <div class="form-row mb-3">
                    <div class="col-md-10">
                      <label for="curp">Curp<font color="red">*</font></label>
                      <input id="curp" name="curp" type="text" class="form-control" 
                        value="${params?.curp?:usuario?.persona?.curp}" 
                        required onkeypress="return esCurp(event, this.id)">
                    </div>
                    <div class="col-md-2 align-self-end">
                        <input class="btn btn-primary" id="btnValidar" name="validarCurp" type="button" value="Validar"/>
                    </div>
                </div>
                <span for="curp"><font color="blue" id="msgOk"></font></span>
                <span for="curp"><font color="red" id="msgError"></font></span>
                
                <div class="form-group mt-3">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${params?.nombre?:usuario?.persona?.nombre}" required readonly>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="primerApellido">Primer Apellido <font color="red">*</font></label>
                        <input id="primerApellido" name="primerApellido" type="text" class="form-control" value="${params?.primerApellido?:usuario?.persona?.primerApellido}" required readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese el primer apellido.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="segundoApellido">Segundo Apellido</label>
                        <input id="segundoApellido" name="segundoApellido" type="text" class="form-control"  value="${params?.segundoApellido?:usuario?.persona?.segundoApellido}" readonly>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="entidadNacimiento">Entidad de Nacimiento <font color="red">*</font></label>
                        <input id="entidadNacimiento" name="entidadNacimiento" type="text" class="form-control" value="${params?.entidadNacimiento?:usuario?.persona?.entidadNacimiento}" required readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese la entidad de nacimiento.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fechaNacimiento">Fecha de Nacimiento <font color="red">*</font></label>
                        <input id="fechaNacimiento" name="fechaNacimiento" type="text" class="form-control"  value="${params?.fechaNacimiento?:usuario?.persona?.fechaNacimiento}" readonly>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="sexo">Sexo <font color="red">*</font></label>
                        <input id="sexo" name="sexo" type="text" class="form-control"  value="${params?.sexo?:usuario?.persona?.sexo}" readonly>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="correoElectronico">Correo electrónico <font color="red">*</font></label>
                        <input id="correoElectronico" name="correoElectronico" type="text" class="form-control" value="${params?.correoElectronico?:usuario?.persona?.correoElectronico}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el correo electrónico.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="telefono">Teléfono <font color="red">*</font></label>
                        <input id="telefono" name="telefono" type="text" class="form-control"  value="${params?.telefono?:usuario?.persona?.telefono}" required onkeypress="return esNumeroTelefonico(event, this.id)">
                        <div class="invalid-feedback">
                            Por favor ingrese el teléfono.
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="titulo">Título</label>
                    <input id="titulo" name="titulo" type="text" class="form-control"  value="${params?.titulo?:usuario?.persona?.titulo}">
                    <div class="invalid-feedback">
                        Por favor ingrese el título.
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="cargo">Cargo <font color="red">*</font></label>
                    <input id="cargo" name="cargo" type="text" class="form-control"  value="${params?.cargo?:usuario?.cargo}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el cargo.
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card " >
            <div class="card-header">
                Función en plataforma
            </div>
            <div class="card-body py-4 px-4">
                
                <div class="form-row mb-4">
                    <input type="text" id="listaRoles" name="listaRoles" value="${params?.listaRoles?:''}" hidden >
                    <div class="col-md-10">
                        <g:select  class="form-control" id="roles" name="roles" from="${roles}" optionKey="id" optionValue="nombre" />
                        <input type="text" id="rolId" name="rolId" hidden>
                    </div>
                    <div class="col-md-2 align-self-end">
                        <button type="button" class="btn btn-primary" onclick="agregarRol()">Agregar</button>
                    </div>
                </div>
                
                <table id="tablaRoles" class="table table-striped">
                    <thead>
                        <tr>
                            <th>Función</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tBody>
                    </tBody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card" >
            <div class="card-header">
                Instituciones
            </div>
            <div class="card-body py-4 px-4">
                <div class="form-row mb-4">
                    <input type="text" id="listaInstituciones" name="listaInstituciones" value="${params?.listaInstituciones?:''}" hidden>
                    <div class="col-md-10">
                        <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="" />
                        <input type="text" id="institucionId" name="institucionId" hidden>
                    </div>
                    <div class="col-md-2 align-self-end">
                        <button type="button" class="btn btn-primary" onclick="agregarInstitucion()">Agregar</button>
                    </div>
                </div>
                
                <table id="tablaInstituciones" class="table table-striped">
                    <thead>
                        <tr>
                            <th>Institución</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tBody>
                    </tBody>
                </table>
            </div>
        </div>
    </div>
</div>

