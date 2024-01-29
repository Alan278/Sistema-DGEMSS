

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'sieges.Usuario'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'sieges.UsuarioRol'
grails.plugin.springsecurity.authority.className = 'sieges.Rol'
grails.plugin.springsecurity.logout.postOnly = false


def autenticadores = [
    "ROLE_DIRECTOR_ESCUELA",
    "ROLE_AUTENTICADOR_DGEMSS",
    "ROLE_PRESIDENTE_ESCUELA",
    "ROLE_SECRETARIO_ESCUELA",
    "ROLE_VOCAL_ESCUELA",
]

def supervisoresDgemss = [
    "ROLE_SUPERVISOR_POSGRADO",
    "ROLE_SUPERVISOR_SUPERIOR",
    "ROLE_SUPERVISOR_MEDIA",
    "ROLE_SUPERVISOR_MEDIA_PUBLICA",
    "ROLE_SUPERVISOR_TECNICA",
    "ROLE_SUPERVISOR_TECNICA_PUBLICA",
    "ROLE_SUPERVISOR_CONTINUA",
]

def usuariosEscuela = [
    "ROLE_GESTOR_ESCUELA",
    "ROLE_DIRECTOR_ESCUELA",
    "ROLE_PRESIDENTE_ESCUELA",
    "ROLE_SECRETARIO_ESCUELA",
    "ROLE_VOCAL_ESCUELA",
]

def todos = [
    "ROLE_ADMIN",
    "ROLE_SUPERVISOR_POSGRADO",
    "ROLE_SUPERVISOR_SUPERIOR",
    "ROLE_SUPERVISOR_MEDIA",
    "ROLE_SUPERVISOR_MEDIA_PUBLICA",
    "ROLE_SUPERVISOR_TECNICA",
    "ROLE_SUPERVISOR_TECNICA_PUBLICA",
    "ROLE_SUPERVISOR_CONTINUA",
    "ROLE_GESTOR_ESCUELA",
    "ROLE_DIRECTOR_ESCUELA",
    "ROLE_RECEPTOR",
    "ROLE_REVISOR",
    "ROLE_REVISOR_PUBLICA",
    "ROLE_AUTENTICADOR_DGEMSS",
    "ROLE_PRESIDENTE_ESCUELA",
    "ROLE_SECRETARIO_ESCUELA",
    "ROLE_VOCAL_ESCUELA",
]


grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    [pattern: '/',               access: ['permitAll']],
    [pattern: '/error',          access: ['permitAll']],
    [pattern: '/index',          access: ['permitAll']],
    [pattern: '/index.gsp',      access: ['permitAll']],
    [pattern: '/shutdown',       access: ['permitAll']],
    [pattern: '/assets/**',      access: ['permitAll']],
    [pattern: '/**/js/**',       access: ['permitAll']],
    [pattern: '/**/css/**',      access: ['permitAll']],
    [pattern: '/**/images/**',   access: ['permitAll']],
    [pattern: '/**/favicon.ico', access: ['permitAll']],

    [pattern: '/alumno/*',                           access: ['ROLE_GESTOR_ESCUELA']],
    [pattern: '/asignatura/*',                       access: supervisoresDgemss],
    [pattern: '/asignaturaExterna/*',                access: supervisoresDgemss],
    [pattern: '/bitacora/*',                         access: ['ROLE_ADMIN']],
    [pattern: '/carrera/*',                          access: supervisoresDgemss],
    [pattern: '/carreraExterna/*',                   access: ['ROLE_ADMIN']],
    [pattern: '/certificado/consultarCertificado',   access: ['permitAll']],
    [pattern: '/constanciaservicio/consultarConstancia',   access: ['permitAll']],
    [pattern: '/NotificacionProfesional/consultarNotificacion',   access: ['permitAll']],
    [pattern: '/actaprofesional/consultarActa',      access: ['permitAll']],
    [pattern: '/certificado/*',                      access: usuariosEscuela + ['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_SUPERVISOR', 'ROLE_AUTENTICADOR_DGEMSS']],
    [pattern: '/cicloEscolar/*',                     access: ['ROLE_GESTOR_ESCUELA', 'ROLE_RECEPTOR']],
    [pattern: '/constanciaservicio/*',               access: usuariosEscuela + ['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_SUPERVISOR', 'ROLE_AUTENTICADOR_DGEMSS']],
    [pattern: '/NotificacionProfesional/*',          access: usuariosEscuela + ['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_SUPERVISOR', 'ROLE_AUTENTICADOR_DGEMSS']],
    [pattern: '/actaprofesional/*',                  access: usuariosEscuela + ['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_SUPERVISOR', 'ROLE_AUTENTICADOR_DGEMSS',"ROLE_VOCAL_ESCUELA","ROLE_SECRETARIO_ESCUELA","ROLE_PRESIDENTE_ESCUELA"]],
    [pattern: '/curso/*',                            access: ['ROLE_SUPERVISOR_CONTINUA']],
    [pattern: '/diplomado/*',                        access: ['ROLE_SUPERVISOR_CONTINUA']],
    [pattern: '/equivalencias/*',                    access: ['ROLE_ADMIN']],
    [pattern: '/evaluacion/*',                       access: ['ROLE_GESTOR_ESCUELA']],
    [pattern: '/institucion/*',                      access: supervisoresDgemss + ['ROLE_ADMIN']],
    [pattern: '/institucionExterna/*',               access: supervisoresDgemss],
    [pattern: '/personalInstitucional/*',            access: supervisoresDgemss],
    [pattern: '/planEstudios/*',                     access: supervisoresDgemss],
    [pattern: '/formacion/*',                        access: supervisoresDgemss],
    [pattern: '/planEstudiosExterno/*',              access: supervisoresDgemss],
    [pattern: '/reporte/*',                          access: ['ROLE_ADMIN']],
    [pattern: '/respaldo/*',                         access: ['ROLE_ADMIN']],
    [pattern: '/revalidacion/*',                     access: ['ROLE_ADMIN']],
    [pattern: '/redireccion/*',                      access: todos],
    [pattern: '/usuario/*',                          access: ['ROLE_ADMIN']],
    [pattern: '/usuario/perfil',                     access: todos],
    [pattern: '/usuario/modificacionContrasena',     access: todos],
    [pattern: '/usuario/modificarContrasena',        access: todos],
    [pattern: '/usuario/confirmacionUsuario',        access: ['permitAll']],
    [pattern: '/usuario/confirmarUsuario',           access: ['permitAll']],
    [pattern: '/usuario/recuperacionCuenta',         access: ['permitAll']],
    [pattern: '/usuario/enviarCorreoRecuperacion',   access: ['permitAll']],
    [pattern: '/usuario/restablecimientoContrasena', access: ['permitAll']],
    [pattern: '/usuario/restablecerContrasena',      access: ['permitAll']],
    [pattern: '/pago/*',                             access: ['ROLE_RECEPTOR']],
    [pattern: '/certificacion/*',                    access: ['ROLE_RECEPTOR']],
    [pattern: '/pagoNotificacion/*',                 access: ['ROLE_RECEPTOR']],
    [pattern: '/pagoActa/*',                         access: ['ROLE_RECEPTOR']],
    [pattern: '/pagoConstancia/*',                   access: ['ROLE_RECEPTOR']],
    [pattern: '/consultaPago/validarPago',           access: ['ROLE_RECEPTOR']],
    [pattern: '/inspeccionVigilancia/*',             access: ['ROLE_RECEPTOR']],
    [pattern: '/alumno/validarAlumno',               access: ['ROLE_GESTOR_ESCUELA']],
    [pattern: '/certificado/vistaValidarFirma',      access: ['permitAll']],
    [pattern: '/firmaElectronica/*',                 access: autenticadores],
    [pattern: '/dashBoard/*',                        access: "ROLE_AUTENTICADOR_DGEMSS"],
    [pattern: '/tipoTramite/*',                      access: "ROLE_ADMIN"],
    [pattern: '/uma/*',                              access: "ROLE_ADMIN"],

]

grails.plugin.springsecurity.filterChain.chainMap = [
    [pattern: '/assets/**',      filters: 'none'],
    [pattern: '/**/js/**',       filters: 'none'],
    [pattern: '/**/css/**',      filters: 'none'],
    [pattern: '/**/images/**',   filters: 'none'],
    [pattern: '/**/favicon.ico', filters: 'none'],
    [pattern: '/**',             filters: 'JOINED_FILTERS']
]

grails {
	mail {
		host = "smtp.gmail.com"
		port = 465
		username = "tituloelectronico@morelos.gob.mx"
		password = "Du5F07me6V"
		props = ["mail.smtp.auth":"true",
				 "mail.smtp.socketFactory.port":"465",
				 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
				 "mail.smtp.socketFactory.fallback":"false"]
	}
}
