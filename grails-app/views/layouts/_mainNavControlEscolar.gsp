<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        def fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal + 1)
        metodo = uri.substring(fisrtDiagonal + 1, secondDiagonal);
    }


    def modulos = [
        "cicloEscolar",
        "alumno",
        "evaluacion",
    ]
%>

<a
    class = "py-3 px-4 text-muted border-right ${modulos.contains(controllerName)?'active':''}"
    href = "${createLink(uri: 'cicloEscolar/listar')}">
    CONTROL ESCOLAR
</a>