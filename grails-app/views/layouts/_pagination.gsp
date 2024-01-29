<div class="row">
    <div class="col-md-12 text-center">

        <nav class="mt-2">
            <ul class="pagination justify-content-center">
                <g:set var="tmpOffset" value="${0}" />
                <li class="page-item <g:if test="${(offset?:"0").toInteger() <= 0}">disabled</g:if>">
                    <g:if test="${(offset?:"0").toInteger() > 0}">
                        <g:link onclick="mostrarProceso();" uri="${linkUri}" params="${linkParams + [max: (max?:"0").toInteger(), offset: (offset?:"0").toInteger() - (max?:"0").toInteger()]}" class="page-link btn-outline-primary">
                            Anterior
                        </g:link>
                    </g:if>
                    <g:else>
                        <span class="page-link btn-outline-primary">Anterior</span>
                    </g:else>
                </li>

                <g:set var="paginas" value="${Math.ceil(count / (max?:"0").toInteger())}" />
                <g:each in="${(1..(paginas > 0? paginas : 1))}" var="pagina">
                    <li class="page-item <g:if test="${(offset?:"0").toInteger() == tmpOffset}">active</g:if>">
                        <g:link onclick="mostrarProceso();" uri="${linkUri}" params="${linkParams + [max: (max?:"0").toInteger(), offset: tmpOffset]}" class="page-link btn-outline-primary">
                            ${pagina}
                        </g:link>
                    </li>
                    <g:set var="tmpOffset" value="${tmpOffset + (max?:"0").toInteger()}" />
                </g:each>

                <li class="page-item <g:if test="${(offset?:"0").toInteger() >= tmpOffset - (max?:"0").toInteger()}">disabled</g:if>">
                    <g:if test="${(offset?:"0").toInteger() < tmpOffset - (max?:"0").toInteger()}">
                        <g:link onclick="mostrarProceso();" uri="${linkUri}" params="${linkParams + [max: (max?:"0").toInteger(), offset: (offset?:"0").toInteger() + (max?:"0").toInteger()]}" class="page-link btn-outline-primary">
                            Siguiente
                        </g:link>
                    </g:if>
                    <g:else>
                        <span class="page-link btn-outline-primary">Siguiente</span>
                    </g:else>
                </li>
            </ul>
        </nav>

    </div>
</div>