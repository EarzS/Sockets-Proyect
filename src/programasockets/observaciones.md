- El servidor UDP funciona como servidor eco, es decir, reenvia el mismo mensaje
que le es recibido.

- El servidor UDP abre una conexion en un cierto puerto, no le interesa quien se
conecte, la unica funcion que cumple es reenviar el mismo mensaje que le es 
recibido.

- El cliente UDP se le debe especificar por argumentos en la linea de comandos 
el mensaje y el nombre del host, en nuestro caso, como corrimos el cliente y 
servidor en la misma maquina, usamos el mismo ip y hostname.

- Una vez el cliente envia la peticion, el servidor recibe el paquete, el 
servidor usa la direccion obtenida del paquete para enviar una respuesta 
con el mismo mensaje. El cliente entonces espera la respuesta del servidor, 
una vez recibido el paquete de respuesta el cliente lo imprime en la linea 
de comandos y finaliza el programa.

- Algo importante es que tanto el cliente como el servidor nunca establecen 
una conexion directa entre ellos mismos, es en los paquetes en donde se cargan 
la direccion del destinatario.

- Su velocidad de respuesta es inmediata cuando la direccion destino es la 
correcta.

- Si el cliente envia un paquete al servidor cuando este desactivado, el 
cliente esperara indefinidamente la llegada del paquete.

###############################################################################

# Modifique el programa cliente para leer repetidamente una entrada de usuario. 
Envíe esta entrada al servidor y adapte el código del servidor para responder 
al cliente. Establezca un tiempo de espera (timeouts) en el socket para que 
el cliente se entere cuando el servidor no responde.

- En la clase UDPCliente, para recibir el input, se utilizo la clase Scanner,
y dentro de un ciclo iterativo, se lee la entrada del teclado hasta que la 
entrada sea "EOF".

- Se edito el codigo UDPCliente para que el usuario solo ingrese como argumento 
el hostname de la maquina al ejecutar el bytecode.


- Se creo un metodo que permite al servidor enviar un tipo de respuesta en 
base al mensaje del cliente.

- Para establecer el timeout se utilizo setSoTimeout

#############################################################################

Para realizar la implementacion en el TCP fue un poco diferente, debido a los
streams para entrada y salidas de datos, se opto por crear hilos para los
clientes.