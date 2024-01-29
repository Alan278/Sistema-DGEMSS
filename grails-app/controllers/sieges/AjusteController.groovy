package sieges

import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql
import sieges.PlanEstudios
import sieges.Formacion
import sieges.Asignatura

class AjusteController {

    def certificadoService
    def dataSource
    def ajusteService
    def tipoFormacionService

    @Secured('ROLE_ADMIN')
    def actualizarEditable(){
        // def EDITABLE = EstatusRegistro.get(1)
        // def NO_EDITABLE = EstatusRegistro.get(2)
        // def BLOQUEADO = EstatusRegistro.get(3)

        // def certificados = Certificado.findAllByActivo(true)

        // def estatusEditable = [1, 3, 6]
        // def estatusNoEditable = [2, 4, 5, 7, 8]
        // def estatusBloqueado = [9]

        // certificados.each{ certificado ->
        //     def alumno = certificado.alumno
        //     def estatusRegistro

        //     if(certificado.estatusCertificado.id in estatusEditable) estatusRegistro = EDITABLE
        //     if(certificado.estatusCertificado.id in estatusNoEditable) estatusRegistro = NO_EDITABLE
        //     if(certificado.estatusCertificado.id in estatusBloqueado) estatusRegistro = BLOQUEADO

        //     alumno.estatusRegistro = estatusRegistro
        //     alumno.save()

        //     for(evaluacion in alumno.evaluaciones){
        //         if(!evaluacion.activo) continue
        //         evaluacion.estatusRegistro = estatusRegistro
        //         evaluacion.save(flush: true)
        //     }

        //     for(registro in alumno.ciclosEscolares){
        //         if(!registro.activo) continue
        //         registro.estatusRegistro = estatusRegistro
        //         registro.save(flush: true)
        //     }
        // }

        // def ciclosEscolares = CicloEscolar.findAllByActivo(true)
        // for(ciclo in ciclosEscolares){
        //     def sql = new Sql(dataSource)
        //     def numCiclos = sql.rows("select max(estatus_registro_id) as mx from alumno_ciclo_escolar where activo = true and ciclo_escolar_id = ${ciclo.id} and estatus_registro_id <> 1 group by ciclo_escolar_id")
        //     if(!numCiclos) continue
            
        //     render "<li>${ciclo.nombre} : ${numCiclos}</li>"
        //     ciclo.estatusRegistro = EstatusRegistro.get(numCiclos[0].mx)
        //     ciclo.save(flush:true)
        // }
        // render "ok"
    }

    @Secured('ROLE_ADMIN')
    def actualizarCiclos(){
        // def ciclosEscolares = CicloEscolar.findAllByActivo(true)
        // for(ciclo in ciclosEscolares){
        //     def sql = new Sql(dataSource)
        //     def numCiclos = sql.rows("select max(estatus_registro_id) as mx from alumno_ciclo_escolar where activo = true and ciclo_escolar_id = ${ciclo.id} and estatus_registro_id <> 1 group by ciclo_escolar_id")
        //     if(!numCiclos) continue

        //     render "<li>${ciclo.nombre} : ${numCiclos}</li>"
        //     ciclo.estatusRegistro = EstatusRegistro.get(numCiclos[0].mx)
        //     ciclo.save(flush:true)
        // }
        // render "ok"
    }

    @Secured('ROLE_ADMIN')
    def probarJson(){
        // render ajusteService.probarJson()
    }

    @Secured('ROLE_ADMIN')
    def pruebaEnum(){
        // render EstatusUsuarioIds.CREADO.ordinal()
    }

    @Secured('ROLE_ADMIN')
    def actualizarCarreraAPlan(){
        // def alumnos = Alumno.list()

        // for(alumno in alumnos){
        //     // render "<p> <b>"+ alumno.persona.nombre +"</b>: "+ alumno.carrera.id +" - "+ alumno.carrera.nombre +"</p>"
        //     def planEstudios = PlanEstudios.findByCarreraAndActivo(alumno.carrera, true)
        //     // render "<p>"+ planEstudio.id +" - "+ planEstudio.nombre +"</p>"
        //     alumno.planEstudios = planEstudios
        //     alumno.save(flush: true)
        // }

        // def ciclos = CicloEscolar.list()

        // for(ciclo in ciclos){
        //     // render "<p> <b>"+ alumno.persona.nombre +"</b>: "+ alumno.carrera.id +" - "+ alumno.carrera.nombre +"</p>"
        //     def planEstudios = PlanEstudios.findByCarreraAndActivo(ciclo.carrera, true)
        //     // render "<p>"+ planEstudio.id +" - "+ planEstudio.nombre +"</p>"
        //     ciclo.planEstudios = planEstudios
        //     ciclo.save(flush: true)
        // }

        // render "ok"
    }

    @Secured('ROLE_ADMIN')
    def actualizarRvoeDeCarreraAPlan(){
        def carreras = Carrera.list()

        for(carrera in carreras){
            if(carrera.planesEstudio){
                for(plan in carrera.planesEstudio){
                    plan.rvoe = carrera.rvoe
                    plan.fechaRvoe = carrera.fechaRvoe
                    plan.save(flush: true)
                    render plan
                }
                render "<hr>"
            }
        }

        // render carreras
    }

    @Secured('ROLE_ADMIN')
    def pruebaMap(){
        def map = [
            'general': [1,2,3],
            'general1': [1,2,3],
            'general2': [1,2,3],
        ]

        map = map + ["asd": []]

        render map.asd


        map.each{ item ->
            println "$item"
        }

        for(item in map){
            render item.value
        }

        render (map.containsKey('general'))

        render "ok"
    }

    @Secured('ROLE_ADMIN')
    def crearFormacionesPorPlan(){
        def planes = PlanEstudios.findAllByActivo(true)

        def basica = TipoFormacion.get(tipoFormacionService.BASICA_ID)
        def paraescolares = TipoFormacion.get(tipoFormacionService.PARAESCOLARES_ID)

        render planes

        for(plan in planes){
            def formacionBasica = Formacion.findByTipoFormacionAndPlanEstudiosAndActivo(basica, plan, true)
            def formacionParaescolares = Formacion.findByTipoFormacionAndPlanEstudiosAndActivo(paraescolares, plan, true)

            if(!formacionBasica){
                formacionBasica = new Formacion(
                    nombre: "BÁSICA",
                    requerida: true,
                    general: true,
                    tipoFormacion: basica,
                    planEstudios: plan,
                ).save(flush: true)
            }

            if(!formacionParaescolares){
                formacionParaescolares = new Formacion(
                    nombre: "PARAESCOLARES",
                    requerida: false,
                    general: true,
                    tipoFormacion: paraescolares,
                    planEstudios: plan,
                ).save(flush: true)
            }

            def asignaturas =  Asignatura.findAllByPlanEstudios(plan)

            for(asignatura in asignaturas){
                if(asignatura.formacion.planEstudios != plan){
                    if(asignatura.formacion.tipoFormacion.id == 1){
                        asignatura.formacion = formacionBasica
                    }
                    if(asignatura.formacion.tipoFormacion.id == 2){
                        asignatura.formacion = formacionParaescolares
                    }

                    asignatura.save(flush:true)
                }
            }
        }


        render "ok"
    }
}
