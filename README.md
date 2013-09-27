<h1>IRCMap C2</h1>

IrcMapC2 es un proyecto OpenSource (C&oacute;digo Abierto) gratuito y de libre distribución según
la licencia GPL que permite visualizar una estructura de red de una manera totalmente 
visual en cualquier Web.<br />
<br>
<div style="text-align: center; width: 600px; margin: 0 auto;">
    <applet code="IrcMapC2.class" width=400 height=400>
	<param name=leafs value="
hub1-nodo11*9,
hub1-nodo12*4,
hub1-nodo13*6,
hub2-nodo21*6,
hub3-nodo31*3,
hub3-nodo41*10,
hub3-nodo51*15,
hub1-hub2*0,
hub2-hub1*4,
hub3-hub1*3">
	<param name=hubs value="
hub1-200x200,
hub2-100x100,
hub3-300x100">
	<param name=image_hubs value="hubs.gif">
	<param name=image_leafs value="leafs.gif">
	<img src="nojava.gif"  alt="TU NAVEGADOR NO TIENE SOPORTE JAVA">
    </applet>
    <br />
    <strong>Ejemplo del IrcMap C2 v.2.0. en funcionamiento.</strong>
</div>
<hr>
<h3>Historial</h3>

Tras la creaci&oacute;n de la red de irc <strong>IRC.CONECTADOS.ORG</strong>, se nos ocurri&oacute; 
preparar una p&aacute;gina web que diese a conocer dicha red y mostrase
las estad&iacute;sticas de uso, canales creados, velocidades, tr&aacute;fico,
etc. Una de las ideas que nos gust&oacute; fue el mapa de red que encontramos
en la web del irc-hispano y que vimos que ser&iacute;a una manera facil
y sencilla de dar a conocer los distintos servidores, as&iacute; como
informar a los usuarios de la red, sobre qu&eacute; servidores est&aacute;n
menos sobrecargados o cuales ofrecen una conexi&oacute;n mejor.

Con motivo de aprender a crear un Applet Java nos propusimos un peque&ntilde;o reto,
crear un applet que muestre la red y aunque, partiendo de cero era bastante
dificil, poco a poco nos ha ido sirviendo para aprender a hacer ciertas
cosillas e implementar algunas nuevas rutinas muy interesantes. :D

<h3>Cambios</h3>

Mayo - 2003 : (<strong>Versi&oacute;n 2.0</strong>)
<ul>
    <li>Creada una segunda versi&oacute;n del Applet, esta vez partiendo completamente 
	    de cero aunque con la misma idea que la primera versi&oacute;n: creando clases 
	    distintas para nodos, conexiones.</li>
    <li>Añadido soporte para varios hubs y la posibilidad de recolocarlos en distintas posiciones dentro del hub.</li>
    <li>Mejorada la velocidad, optimizando la memoria est&aacute;tica asignada.</li>
    <li>Soporte para coloraci&oacute;n de las conexiones (manteniendo el nombre 
	    <em>Edges</em> por antiguedad :P) para la se&ntilde;alizaci&oacute;n de 
	    la velocidad, carga o ancho de banda seg&uacute;n elecci&oacute;n, aunque 
	    aun no se puede configurar por par&aacute;metros.</li>
    <li>Soporte para gr&aacute;ficos con transparencias (GIF, PNG) que permiten
	    ver los nodos como queramos. Aunque tambi&eacute;n funciona con archivos JPG.</li>
    <li>Cambiada la velocidad de <em>Refresh</em> para ver el movimiento de los 
	    nodos y las lineas de conexi&oacute;n con una velocidad &quot;m&aacute;s 
	    aceptable&quot;.</li>
</ul>

Abril - 2003</strong>: (<strong>Versi&oacute;n 1.0, 1.1</strong>)
<ul>
    <li>Creada la primera versi&oacute;n del Applet basada en el ejemplo del Java</li>
    <li>SDK &quot;Graph-Layout&quot; en la que se le implement&oacute; el soporte
	    de gr&aacute;ficos en lugar de recuadros y poco m&aacute;s. Solo permite
	    un &uacute;nico nodo <strong>hub</strong> pero sigue siendo lento y tarda
	    demasiado en cargar en cualquier navegador.</li>
</ul>