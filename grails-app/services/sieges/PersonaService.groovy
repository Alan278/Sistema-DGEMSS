package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * Servicio que permite la administración de personas
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class PersonaService {
    def usuarioService
	def springSecurityService
    /**
     * Obtiene las personas activos y que son alumnos con parámetros de filtrado y paginado
     * @param institucionId (opcional)
     * id de la institución
     * @param carreraId (opcional)
     * id de la carrera
     * @param search (opcional)
     * id del ciclo escolar
     * @return resultado
     * resultado con el estatus, mensaje y datos de la persona
     */
    def listarAlumnos(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def usuario = Usuario.get(springSecurityService.principal.id)
		def roles = usuarioService.obtenerRoles()

        if(!params.max) params.max = 3
		if(!params.offset) params.offset = 0
        if(!params.max) params.max = 3
        if(!params.offset) params.offset = 0

        def institucionId
		if(roles.contains('ROLE_ADMIN')){
			if(params.institucionId){
				institucionId = Integer.parseInt(params.institucionId)
			}
		}

        def carreraId
        if(params.carreraId) carreraId = Integer.parseInt(params.carreraId)

        def criteria = {
            createAlias("cicloEscolar.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("cicloEscolar.planEstudios.carrera", "c", CriteriaSpecification.INNER_JOIN)
            createAlias("cicloEscolar.planEstudios.carrera.institucion", "i", CriteriaSpecification.INNER_JOIN)
            createAlias("cicloEscolar", "ciclo", CriteriaSpecification.LEFT_JOIN)
            createAlias("persona", "p", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.search){
                    or{
                        ilike("p.nombre", "%${params.search}%")
                        ilike("p.primerApellido", "%${params.search}%")
                        ilike("p.segundoApellido", "%${params.search}%")
                    }
                }
                if(roles.contains('ROLE_ADMIN') && params.institucionId){
 				    eq("i.id", institucionId)
 				}else{
					or{
						usuario.instituciones.each{ registro ->
							eq("i.id", registro.institucion.id)
						}
					}
				}   
                if(params.carreraId){
                    eq("carrera.id", carreraId)
                }
            }
            projections{
                groupProperty('p.id')
                groupProperty('p.curp')
                groupProperty('p.nombre')
                groupProperty('p.primerApellido')
                groupProperty('p.segundoApellido')
                groupProperty('p.numCertificados')
            }
        }

        def alumnos = Alumno.createCriteria().list(params,criteria)

        if(alumnos.totalCount <= 0){
            resultado.mensaje = 'No se encontraron alumnos'
            resultado.datos = alumnos
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Alumnos consultados exitosamente'
        resultado.datos = alumnos

        return resultado
    }

    /**
     * Obtiene una persona específica
     * @param id (Requerido)
     * Identificador de la persona
     */
    def consultar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def persona = Persona.get(params.id)

        if(!persona){
            resultado.mensaje = 'Persona no encontrada'
            return resultado
        }

        if(!persona.activo){
            resultado.mensaje = 'Persona inactiva'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Persona encontrada exitosamente'
        resultado.datos = persona

        return resultado
    }
}
