<tr>
    <td class="align-middle">
        ${tipoTramite.id}
    </td>
    <td class="align-middle">
        ${tipoTramite.nombre}
    </td>
    <td class="align-middle">
        ${tipoTramite.idConcepto}
    </td>
    <td class="align-middle">
        ${tipoTramite.costoUmas}
    </td>
    <td class="align-middle">
        $${(tipoTramite.costoUmas * uma.valor).round(0)}
    </td>
    <td class="align-middle" align="end">
        <g:link
            action="modificacion"
            id="${tipoTramite?.id}"
            class="btn btn-outline-primary btn-sm"
            onclick="mostrarProceso()">
            <i class="fa fa-pencil"></i>
        </g:link>
    </td>
</tr>