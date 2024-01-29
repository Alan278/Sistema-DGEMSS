<div class="container">
    <g:if test="${mensaje}">
        <g:if test="${estatus}">
            <div class="alert alert-success" role="alert">
                ${mensaje}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </g:if>
        <g:else>
            <div class="alert alert-danger" role="alert">
                ${mensaje}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </g:else>
    </g:if>
</div>