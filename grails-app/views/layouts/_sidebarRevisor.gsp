<li class="nav-item mb-2">
    <a
        onclick = "mostrarProceso();" 
        class = "nav-link-b ${controllerName == 'certificado'?'active':''}" 
        href = "${createLink(uri: 'certificado/listarCertificadosRevisar')}">
        Certificados a revisar
    </a>
</li>