<div class="container">
    <g:if test="${!estatus}">
        <g:if test="${mensaje}">
            <div class="alert alert-danger" role="alert">
                ${mensaje}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </g:if>
    </g:if>
</div>