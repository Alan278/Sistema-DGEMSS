<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Nuevas Equivalencias</title>
</head>

<body>
    <div class="row mb-5">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Expedición de Equivalencias de Estudios - Generar
            </h3>
        </div>
    </div>
    <div class="container">
        <g:if test="${flash.estatus}">
            <div class="alert alert-success" role="alert">
                ${flash.mensaje}
            </div>
        </g:if>
        <g:else>
            <g:if test="${flash.mensaje}">
                <div class="alert alert-danger" role="alert">
                    ${flash.mensaje}
                </div>
            </g:if>
        </g:else>
    </div>
    <div class="container">
        <div class="alert alert-light" role="alert">
          Los campos marcados con * son obligatorios.
        </div>
    </div>

    <div class="container mb-5">
        <form action="/equivalencias/registrar" class="needs-validation" novalidate>
            <input type="text" id="materias" name="materias" hidden>
                <div class="form-group">
                    <label for="institucion">Nombre de la Institución <font color="red">*</font></label>
                    <input id="institucion" name="institucion" type="text" class="form-control"  value="${params.institucion}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre de la institución.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nivelExterno">Nivel de procedencia <font color="red">*</font></label>
                    <input id="nivelExterno" name="nivelExterno" type="text" class="form-control"  value="${params?.nivelExterno}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel de procedencia.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nivelInterno">Nivel educativo equiparable <font color="red">*</font></label>
                    <input id="nivelInterno" name="nivelInterno" type="text" class="form-control"  value="${params?.nivelInterno}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel equiparable.
                    </div>
                </div>
                <div class="form-group">
                    <label for="cicloEscolar">Ciclo escolar <font color="red">*</font></label>
                    <input id="cicloEscolar" name="cicloEscolar" type="text" class="form-control"  value="${params?.cicloEscolar}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el ciclo escolar.
                    </div>
                </div>
                <div class="form-row mb-5">
                    <div class="form-group col-md-6">
                        <label for="folio">Folio <font color="red">*</font></label>
                        <input id="folio" name="folio" type="text" class="form-control"  value="${params?.folio}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el folio.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="expediente">Número de Expediente <font color="red">*</font></label>
                        <input id="expediente" name="expediente" type="text" class="form-control"  value="${params?.expediente}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el número de expediente.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="lugarExpedicion">Lugar de Expedición <font color="red">*</font></label>
                        <input id="lugarExpedicion" name="lugarExpedicion" type="text" class="form-control"  value="${params?.lugarExpedicion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el lugar de expedición.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fechaExpedicion">Fecha de Expedición <font color="red">*</font></label>
                        <input id="fechaExpedicion" name="fechaExpedicion" type="date" class="form-control"  value="${params?.fechaExpedicion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de expedición.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="cct">C.C.T <font color="red">*</font></label>
                        <input id="cct" name="cct" type="text" class="form-control"  value="${params?.cct}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el cct.
                        </div>
                    </div>
                </div>

                
                <h4>Tabla de equiparación</h4>
                <div class="row mt-4 mb-4">
                    <div class="col-md-4">
                        <input id="asignaturaCursada" name="asignaturaCursada" type="text" class="form-control"  value="${equivalencias?.asignaturaCursada}" placeholder="Asignatura Cursada">
                    </div>
                    <div class="col-md-4">
                        <input id="asignaturaEquivalente" name="asignaturaEquivalente" type="text" class="form-control"  value="${equivalencias?.asignaturaEquivalente}" placeholder="Asignatura Equivalente">
                    </div>
                    <div class="col-md-2">
                        <input id="calificacion" name="calificacion" type="text" class="form-control"  value="${equivalencias?.calificacion}" placeholder="Calificación" onkeypress="return esCalificacion(event, this.id);">
                    </div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-primary" onclick="agregarRevalidacion()">Agregar</button>
                    </div>
                </div>    
                    
                        <table id="tabla" class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Asignatura cursada</th>
                                    <th>Asignatura equivalente</th>
                                    <th>Calificación</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tBody>
                            </tBody>
                        </table>
                
                </br>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="/equivalencias/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <asset:javascript src="jquery-editable-select.js"/>
                <asset:javascript src="jquery-editable-select.min.js"/>
                <asset:javascript src="filter-buttons.js"/>
                <asset:javascript src="validations.js"/>
        </form>
    </div>

    <script>
        var asignaturas = []

        function mostrarTabla(){

            var asignaturasJson = JSON.stringify(asignaturas);
            console.log(asignaturasJson)
            var asignaturasInput = document.getElementById("materias")
            asignaturasInput.value = asignaturasJson

            $("#tabla tr:gt(0)").remove()

            asignaturas.forEach((revalidacion, indice) => {
                var aCursada_node = document.createTextNode(revalidacion.asignaturaCursada);
                var aEquivalente_node = document.createTextNode(revalidacion.asignaturaEquivalente);
                var calificacion_node = document.createTextNode(revalidacion.calificacion);

                var td_aCursada_node = document.createElement("td");
                td_aCursada_node.appendChild(aCursada_node);
                var td_aEquivalente_node = document.createElement("td");
                td_aEquivalente_node.appendChild(aEquivalente_node);
                var td_calificacion_node = document.createElement("td");
                td_calificacion_node.appendChild(calificacion_node);

                // Eliminar  <i class= "fa fa-trash"></i>
                var ele_a = document.createElement("a");
                ele_a.setAttribute("href","javascript:void(0);");
                ele_a.setAttribute("onclick","del_tr(" + indice + ");");
                ele_a.setAttribute("class","btn btn-danger");
                var del_node = document.createElement("i");
                del_node.setAttribute("class","fa fa-trash");

                ele_a.appendChild(del_node);
                var td_del_node = document.createElement("td");
                td_del_node.appendChild(ele_a);
                
                var tr_node = document.createElement("tr");
                tr_node.appendChild(td_aCursada_node);
                tr_node.appendChild(td_aEquivalente_node);
                tr_node.appendChild(td_calificacion_node);
                tr_node.appendChild(td_del_node);

                var table_node = document.getElementById("tabla");
                table_node.appendChild(tr_node);
            });
        }
 
    
        function agregarRevalidacion(){
            var asignaturaCursada= document.getElementById('asignaturaCursada');
            var asignaturaEquivalente = document.getElementById('asignaturaEquivalente');
            var calificacion = document.getElementById('calificacion');

            if(asignaturaCursada.value == "" || asignaturaEquivalente.value == "" || calificacion.value == ""){
                return
            }

            var revalidacion = {
                "asignaturaCursada": asignaturaCursada.value,
                "asignaturaEquivalente": asignaturaEquivalente.value,
                "calificacion": calificacion.value
            }

            asignaturas.push(revalidacion)

            console.log(asignaturas)

            asignaturaCursada.value = ""
            asignaturaEquivalente.value = ""
            calificacion.value = ""

            mostrarTabla()

        }

        del_tr = function (indice) {
            asignaturas.splice(indice, 1)
            console.log(asignaturas)

            mostrarTabla()
        }
    </script>
</body>
</html>

