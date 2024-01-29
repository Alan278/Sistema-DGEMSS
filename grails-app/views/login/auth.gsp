<html>
<head>
	<meta name="layout" content="${gspLayout ?: 'main'}"/>
	<title><g:message code='springSecurity.login.title'/></title>
</head>

<body>
	<div class="container">
		<div class="row justify-content-center mt-5">
			<div class="card" style="width: 22rem;">

				<div class="card-header">
					<div class="row justify-content-center">

						<b> Inicio de sesión</b>
					</div>
				</div>

				<div class="card-body">
		
					<g:if test='${flash.message}'>
						<div class="alert alert-danger" role="alert">
							${flash.message}
						</div>
					</g:if>
		
					<form action="${postUrl ?: '/login/authenticate'}" method="POST" id="loginForm" class="cssform" autocomplete="off">
		
						<div class="form-group">
							<label for="username">Usuario:</label>
							<input type="text" class="form-control " name="${usernameParameter ?: 'username'}" id="username" placeholder="Nombre de usuario"/>
						</div>
		
						<div class="form-group">
							<label for="password">Contraseña:</label>
							<input type="password" class="form-control" name="${passwordParameter ?: 'password'}" id="password" placeholder="Contraseña">
						</div>
		
						<div class="form-check" id="remember_me_holder">
							<input type="checkbox" class="form-check-input" name="${rememberMeParameter ?: 'remember-me'}" id="remember_me" <g:if test='${hasCookie}'>checked="checked"</g:if> />
							<label class="form-check-label"	for="remember_me"><g:message code='springSecurity.login.remember.me.label'/></label>
						</div>
		
						<div class="row justify-content-center mt-4">
							<input class="btn btn-primary" type="submit" id="submit" value="Ingresar"/>
						</div>
					</form>
					<div class="mt-4 d-flex justify-content-center">
						<a href="/usuario/recuperacionCuenta">Olvidé mi contraseña</a>
					</div>
				</div>
			</div>
		</div>
	</div>
<script>
(function() {
	document.forms['loginForm'].elements['${usernameParameter ?: 'username'}'].focus();
})();
</script>
</body>
</html>
