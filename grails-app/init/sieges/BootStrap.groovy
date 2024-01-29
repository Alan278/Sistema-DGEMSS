package sieges

import sieges.Rol
import sieges.Usuario
import sieges.UsuarioRol
import sieges.Persona

class BootStrap {

    def init = { servletContext ->

        // Creación de roles
        def roles = [
            [ authority: "ROLE_ADMIN",                      nombre: "Administrador del sistema" ],
            [ authority: "ROLE_SUPERVISOR_POSGRADO",        nombre: "Supervisor de educación de posgrado"],
            [ authority: "ROLE_SUPERVISOR_SUPERIOR",        nombre: "Supervisor de educación superior"],
            [ authority: "ROLE_SUPERVISOR_MEDIA",           nombre: "Supervisor de educación media superior"],
            [ authority: "ROLE_SUPERVISOR_MEDIA_PUBLICA",   nombre: "Supervisor de TBC"],
            [ authority: "ROLE_SUPERVISOR_TECNICA",         nombre: "Supervisor de educación técnica"],
            [ authority: "ROLE_SUPERVISOR_TECNICA_PUBLICA", nombre: "Supervisor de CECAT"],
            [ authority: "ROLE_SUPERVISOR_CONTINUA",        nombre: "Supervisor de educación continua"],
            [ authority: "ROLE_GESTOR_ESCUELA",             nombre: "Gestor de escuela"],
            [ authority: "ROLE_DIRECTOR_ESCUELA",           nombre: "Director de escuela"],
            [ authority: "ROLE_RECEPTOR",                   nombre: "Receptor"],
            [ authority: "ROLE_REVISOR",                    nombre: "Revisor"],
            [ authority: "ROLE_REVISOR_PUBLICA",            nombre: "Revisor de tbc"],
            [ authority: "ROLE_AUTENTICADOR_DGEMSS",        nombre: "Autenticador DGEMSS"],
            [ authority: "ROLE_PRESIDENTE_ESCUELA",         nombre: "Presidente de escuela"],
            [ authority: "ROLE_SECRETARIO_ESCUELA",         nombre: "Secretario de escuela"],
            [ authority: "ROLE_VOCAL_ESCUELA",              nombre: "Vocal de escuela"],
        ]
        roles.each{ rol ->
            if (!Rol.findByAuthority(rol.authority)) {
                def newRol = new Rol(rol)
                newRol.save(flush: true)
            }
        }

        // Creación de niveles
        def niveles = [
            [ nombre: "Superior"],
            [ nombre: "Medio Superior"],
            [ nombre: "Técnico"],
            [ nombre: "Continuo"],
        ]
        niveles.each{ nivel ->
            if (!Nivel.findByNombre(nivel.nombre)) {
                def newNivel = new Nivel(nivel)
                newNivel.save(flush: true)
            }
        }

        // Creación de modalidades
        def modalidades = [
            [ nombre: "Escolarizado"],
            [ nombre: "No Escolarizado"],
            [ nombre: "Virtual"],
            [ nombre: "Mixto"],
        ]
        modalidades.each{ modalidad ->
            if (!Modalidad.findByNombre(modalidad.nombre)) {
                def newModalidad = new Modalidad(modalidad)
                newModalidad.save(flush: true)
            }
        }

        // Creación de areas
        def areas = [
            [ nombre: "Informática"],
            [ nombre: "Medicina"],
            [ nombre: "Deportes"],
        ]
        areas.each{ area ->
            if (!Area.findByNombre(area.nombre)) {
                def newArea = new Area(area)
                newArea.save(flush: true)
            }
        }

        // Creación de periodos
        def periodos = [
            [ nombre: "Trimestral"],
            [ nombre: "Cuatrimestral"],
            [ nombre: "Semestral"],
            [ nombre: "Anual"],
        ]
        periodos.each{ peirodo ->
            if (!Periodo.findByNombre(peirodo.nombre)) {
                def newPeriodo = new Periodo(peirodo)
                newPeriodo.save(flush: true)
            }
        }

        // Creación de turnos
        def turnos = [
            [ nombre: "Matutino"],
            [ nombre: "Vespertino"],
        ]
        turnos.each{ turno ->
            if (!Turno.findByNombre(turno.nombre)) {
                def newTurno = new Turno(turno)
                newTurno.save(flush: true)
            }
        }

        // Creación de estatus alumno
        def estatusAlumno = [
            [ nombre: "Activo"],
            [ nombre: "Baja"],
            [ nombre: "Baja temporal"],
        ]
        estatusAlumno.each{ estatus ->
            if (!EstatusAlumno.findByNombre(estatus.nombre)) {
                def newEstatusAlumno = new EstatusAlumno(estatus)
                newEstatusAlumno.save(flush: true)
            }
        }

        // Creación de cargos institucionales
        def cargos = [
            [ nombre: "Director"],
            [ nombre: "Servicios escolares"],
            [ nombre: "Subdirector"],
        ]
        cargos.each{ cargo ->
            if (!CargoInstitucional.findByNombre(cargo.nombre)) {
                def newCargo = new CargoInstitucional(cargo)
                newCargo.save(flush: true)
            }
        }

        // Creación de estatus de certificados
        def estatusCertificados = [
            [ id: 1, nombre: "GENERADO" ],
            [ id: 2, nombre: "FIRMANDO_ESCUELA" ],
            [ id: 3, nombre: "RECHAZADO_DIRECTOR" ],
            [ id: 4, nombre: "EN_ESPERA" ],
            [ id: 5, nombre: "EN_REVISION" ],
            [ id: 6, nombre: "RECHAZADO_DGEMSS" ],
            [ id: 7, nombre: "FIRMANDO_DGEMSS" ],
            [ id: 8, nombre: "RECHAZADO_AUTENTICADOR" ],
            [ id: 9, nombre: "FINALIZADO" ],
        ]

        def estatusConstancias = [
            [ id: 1, nombre: "GENERADO" ],
            [ id: 2, nombre: "FIRMANDO_ESCUELA" ],
            [ id: 3, nombre: "RECHAZADO_DIRECTOR" ],
            [ id: 4, nombre: "EN_ESPERA" ],
            [ id: 5, nombre: "EN_REVISION" ],
            [ id: 6, nombre: "RECHAZADO_DGEMSS" ],
            [ id: 7, nombre: "FIRMANDO_DGEMSS" ],
            [ id: 8, nombre: "RECHAZADO_AUTENTICADOR" ],
            [ id: 9, nombre: "FINALIZADO" ],
        ]

        def estatusNotificaciones = [
            [ id: 1, nombre: "GENERADO" ],
            [ id: 2, nombre: "FIRMANDO_ESCUELA" ],
            [ id: 3, nombre: "RECHAZADO_DIRECTOR" ],
            [ id: 4, nombre: "EN_ESPERA" ],
            [ id: 5, nombre: "EN_REVISION" ],
            [ id: 6, nombre: "RECHAZADO_DGEMSS" ],
            [ id: 7, nombre: "FIRMANDO_DGEMSS" ],
            [ id: 8, nombre: "RECHAZADO_AUTENTICADOR" ],
            [ id: 9, nombre: "FINALIZADO" ],
        ]

        def estatusActas = [
            [ id: 1, nombre: "GENERADO" ],
            [ id: 2, nombre: "FIRMANDO_ESCUELA" ],
            [ id: 3, nombre: "RECHAZADO_DIRECTOR" ],
            [ id: 4, nombre: "EN_ESPERA" ],
            [ id: 5, nombre: "EN_REVISION" ],
            [ id: 6, nombre: "RECHAZADO_DGEMSS" ],
            [ id: 7, nombre: "FIRMANDO_DGEMSS" ],
            [ id: 8, nombre: "RECHAZADO_AUTENTICADOR" ],
            [ id: 9, nombre: "FINALIZADO" ],
        ]


        estatusCertificados.each{ estatus ->
            if (!EstatusCertificado.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusCertificado(estatus)
                newEstatus.save(flush: true)
            }
        }

        estatusActas.each{ estatus ->
            if (!EstatusActa.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusActa(estatus)
                newEstatus.save(flush: true)
            }
        }

        estatusConstancias.each{ estatus ->
            if (!EstatusConstancia.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusConstancia(estatus)
                newEstatus.save(flush: true)
            }
        }

        estatusNotificaciones.each{ estatus ->
            if (!EstatusNotificacion.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusNotificacion(estatus)
                newEstatus.save(flush: true)
            }
        }

        // Creación de TipoFormacion
        def tiposFormacion = [
            [ nombre: "BASICA"],
            [ nombre: "PARAESCOLARES"],
            [ nombre: "PARA_EL_TRABAJO"],
            [ nombre: "PROPEDEUTICA"],
        ]
        tiposFormacion.each{ tipo ->
            if (!TipoFormacion.findByNombre(tipo.nombre)) {
                def newTipo = new TipoFormacion(tipo)
                newTipo.save(flush: true)
            }
        }

        // Creación de Formaciones
        def formaciones = [
            [ nombre: "Básica", nombreTipoFormacion: "BASICA"],
            [ nombre: "Paraescolares", nombreTipoFormacion: "PARAESCOLARES"],
            [ nombre: "Para el trabajo", nombreTipoFormacion: "PARA_EL_TRABAJO"],
            [ nombre: "Propedéutica", nombreTipoFormacion: "PROPEDEUTICA"],
        ]
        formaciones.each{ formacion ->
            if (!Formacion.findByNombre(formacion.nombre)) {
                def newFormacion = new Formacion(formacion)
                newFormacion.tipoFormacion = TipoFormacion.findByNombre(formacion.nombreTipoFormacion)
                newFormacion.save(flush: true)
            }
        }

        // Creación de estatusRegistro
        def estatusRegistro = [
            [ nombre: "EDITABLE"],
            [ nombre: "NO_EDITABLE"],
            [ nombre: "BLOQUEADO"],
        ]
        estatusRegistro.each{ estatus ->
            if (!EstatusRegistro.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusRegistro(estatus)
                newEstatus.save(flush: true)
            }
        }

        def estatusUsuario = [
            [ id:1, nombre: "CREADO", descripcion: "Usuario creado"],
            [ id:2, nombre: "CONFIRMADO", descripcion: "Email confimado por el usuario"],
            [ id:3, nombre: "ACTIVO", descripcion: "Usuario activo"],
            [ id:4, nombre: "INACTIVO", descripcion: "Usuario inactivo"],
            [ id:5, nombre: "BLOQUEADO", descripcion: "Usuario bloqueado"],
        ]
        estatusUsuario.each{ estatus ->
            if (!EstatusUsuario.findByNombre(estatus.nombre)) {
                def newEstatus = new EstatusUsuario(estatus)
                newEstatus.id = estatus.id
                newEstatus.save(flush: true)
            }
        }

        def tipoTramites = [
            [ id: 1, nombre: "CERTIFICACION", idConcepto: "10603", costoUmas: 0],
            [ id: 2, nombre: "INSPECCION Y VIGILANCIA", idConcepto: "10603", costoUmas: 0],
            [ id: 3, nombre: "NOTIFICACION", idConcepto: "10603", costoUmas: 0],
            [ id: 4, nombre: "CONSTANCIA", idConcepto: "10603", costoUmas: 0],
            [ id: 5, nombre: "ACTA PROFESIONAL", idConcepto: "10603", costoUmas: 0],
        ]
        tipoTramites.each{ tipoTramite ->
            if (!TipoTramite.findByNombre(tipoTramite.nombre)) {
                def newTipoTramite = new TipoTramite(tipoTramite)
                newTipoTramite.id = tipoTramite.id
                newTipoTramite.save(flush: true)
            }
        }

        if (!Uma.get(1)) {
            def uma = new Uma()
            uma.id = 1
            uma.valor = 96.22
            uma.save(flush: true)
        }

        // Creación de tipos de evaluación
        def tiposEvaluacion = [
            [ id: 1, nombre: "ORDINARIA", abreviatura: "O"],
            [ id: 2, nombre: "EXTRAORDINARIA", abreviatura: "E.E."],
            [ id: 3, nombre: "EXAMEN A TÍTULO DE SUFICIENCIA", abreviatura: "E.T.S."],
            [ id: 4, nombre: "RECURSAMIENTO", abreviatura: "Rec."],
            [ id: 5, nombre: "EXAMEN POR DERECHO DE PASANTE", abreviatura: "E.D.P."],
            [ id: 6, nombre: "ASIGNATURA LIBRE", abreviatura: "A.L."],
        ]
        tiposEvaluacion.each{ tipoEvaluacion ->
            if (!TipoEvaluacion.findByNombre(tipoEvaluacion.nombre)) {
                def newTipo = new TipoEvaluacion(tipoEvaluacion)
                newTipo.id = tipoEvaluacion.id
                newTipo.save(flush: true)
            }
        }


        def persona = Persona.findByCurp("DOML000813HOCMRSA7")
        if (!persona) {
            persona = new Persona(
                fechaNacimiento: "2000-08-13",
                sexo: "H",
                nombre: "Luis Elías",
                entidadNacimiento: "OC",
                primerApellido: "Domínguez",
            )
            persona.save(flush: true)
        }

        def usuarioAdmin = Usuario.findByUsername("admin")
        if (!usuarioAdmin) {
            usuarioAdmin = new Usuario(
                username: "admin",
                password: "123",
                accountLocked: false,
                confirmToken: "abc123",
                cargo: "Administrador del sistema",
            )
            usuarioAdmin.persona = persona
            usuarioAdmin.save(flush: true)
        }

        def rolAdmin = Rol.findByAuthority('ROLE_ADMIN')
        if (!UsuarioRol.exists(usuarioAdmin.id, rolAdmin.id)) {
            UsuarioRol.create(usuarioAdmin, rolAdmin, true)
        }
    }
    def destroy = {
    }
}
