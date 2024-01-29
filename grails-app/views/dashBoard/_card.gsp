<div class="col-xl-4 col-lg-6 col-md-6 col-sm-12 col-12 mb-3">
    <div class="card h-100">
        <div class="card-body">
            <div class="mb-4">
                <h5 class="d-inline">
                    <i class="${icon}"></i>
                </h5>
                <h5 class="d-inline">
                    <b>
                        ${title}
                    </b>
                </h5>
            </div>
            <div>
                <g:each in="${datos}" var="seccion">
                    <hr>
                    <g:each in="${seccion}" var="item">
                        <h6 class="pb-1">
                            <b> ${item.title}:</b>
                            <medium class="text-muted">${item.count}</medium>
                        </h6>
                    </g:each>
                </g:each>
            </div>
        </div>
    </div>
</div>