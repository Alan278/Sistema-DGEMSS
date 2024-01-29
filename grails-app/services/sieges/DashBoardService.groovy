package sieges

class DashBoardService {

    def datosCertificados() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]
        def estatusCertificado = EstatusCertificado.get(9)

        resultado[0][0].count = Certificado.countByActivo(true)
        resultado[1][0].count = Certificado.countByEstatusCertificadoAndActivo(estatusCertificado, true)
        resultado[1][1].count = Certificado.countByEstatusCertificadoNotEqualAndActivo(estatusCertificado, true)
        resultado[2][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "H")
            }}
            eq('estatusCertificado', estatusCertificado)
            projections {
                count('id')
            }
        }[0]
        resultado[2][1].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "M")
            }}
            eq('estatusCertificado', estatusCertificado)
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosConstancias() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]
        def estatusConstancia = EstatusConstancia.get(9)

        resultado[0][0].count = ConstanciaServicio.countByActivo(true)
        resultado[1][0].count = ConstanciaServicio.countByEstatusConstanciaAndActivo(estatusConstancia, true)
        resultado[1][1].count = ConstanciaServicio.countByEstatusConstanciaNotEqualAndActivo(estatusConstancia, true)
        resultado[2][0].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "H")
            }}
            eq('estatusConstancia', estatusConstancia)
            projections {
                count('id')
            }
        }[0]
        resultado[2][1].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "M")
            }}
            eq('estatusConstancia', estatusConstancia)
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosNotificaciones() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]
        def estatusNotificacion = EstatusNotificacion.get(9)

        resultado[0][0].count = NotificacionProfesional.countByActivo(true)
        resultado[1][0].count = NotificacionProfesional.countByEstatusNotificacionAndActivo(estatusNotificacion, true)
        resultado[1][1].count = NotificacionProfesional.countByEstatusNotificacionNotEqualAndActivo(estatusNotificacion, true)
        resultado[2][0].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "H")
            }}
            eq('estatusNotificacion', estatusNotificacion)
            projections {
                count('id')
            }
        }[0]
        resultado[2][1].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "M")
            }}
            eq('estatusNotificacion', estatusNotificacion)
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosActas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]
        def estatusActa = EstatusActa.get(12)

        resultado[0][0].count = ActaProfesional.countByActivo(true)
        resultado[1][0].count = ActaProfesional.countByEstatusActaAndActivo(estatusActa, true)
        resultado[1][1].count = ActaProfesional.countByEstatusActaNotEqualAndActivo(estatusActa, true)
        resultado[2][0].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "H")
            }}
            eq('estatusActa', estatusActa)
            projections {
                count('id')
            }
        }[0]
        resultado[2][1].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ persona {
                eq('sexo', "M")
            }}
            eq('estatusActa', estatusActa)
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosCertificadosPublicas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', true)
            }}}}
            projections {
                count('id')
            }
        }[0]

        resultado[1][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', true)
            }}}}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', true)
            }}}}
            estatusCertificado {
                ne("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', true)
            }}}}
            alumno{ persona {
                eq('sexo', "H")
            }}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][1].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', true)
            }}}}
            alumno{ persona {
                eq('sexo', "M")
            }}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosConstanciasPrivadas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            projections {
                count('id')
            }
        }[0]

        resultado[1][0].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusConstancia {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusConstancia {
                ne("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][0].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "H")
            }}
            estatusConstancia {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][1].count = ConstanciaServicio.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "M")
            }}
            estatusConstancia {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosNotificacionesPrivadas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            projections {
                count('id')
            }
        }[0]

        resultado[1][0].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusNotificacion {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusNotificacion {
                ne("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][0].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "H")
            }}
            estatusNotificacion {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][1].count = NotificacionProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "M")
            }}
            estatusNotificacion {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosActasPrivadas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            projections {
                count('id')
            }
        }[0]

        resultado[1][0].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusActa {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusActa {
                ne("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][0].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "H")
            }}
            estatusActa {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][1].count = ActaProfesional.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "M")
            }}
            estatusActa {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        return resultado
    }

    def datosCertificadosPrivadas() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Expedidos",
                    count: 0,
                ],
                [
                    title: "En proceso",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Hombres",
                    count: 0,
                ],
                [
                    title: "Mujeres",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            projections {
                count('id')
            }
        }[0]

        resultado[1][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            estatusCertificado {
                ne("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][0].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "H")
            }}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        resultado[2][1].count = Certificado.withCriteria {
            eq('activo', true)
            alumno{ planEstudios { carrera { institucion {
                eq('publica', false)
            }}}}
            alumno{ persona {
                eq('sexo', "M")
            }}
            estatusCertificado {
                eq("id", 9)
            }
            projections {
                count('id')
            }
        }[0]

        return resultado
    }


    def datosInstituciones() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Privadas",
                    count: 0,
                ],
                [
                    title: "Públicas",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = Institucion.countByActivo(true)
        resultado[1][0].count = Institucion.countByPublicaAndActivo(false, true)
        resultado[1][1].count = Institucion.countByPublicaAndActivo(true, true)

        return resultado
    }

    def datosAlumnos() {
        def resultado = [
            [
                [
                    title: "Total",
                    count: 0,
                ]
            ],
            [
                [
                    title: "Privadas",
                    count: 0,
                ],
                [
                    title: "Públicas",
                    count: 0,
                ]
            ]
        ]

        resultado[0][0].count = Alumno.countByActivo(true)

        resultado[1][0].count = Alumno.withCriteria {
            eq('activo', true)
            planEstudios {
                carrera {
                    institucion {
                        eq('publica', false)
                    }
                }
            }
            projections {
                count('id')
            }
        }[0]

        resultado[1][1].count = Alumno.withCriteria {
            eq('activo', true)
            planEstudios {
                carrera {
                    institucion {
                        eq('publica', false)
                    }
                }
            }
            projections {
                count('id')
            }
        }[0]


        return resultado
    }
}
