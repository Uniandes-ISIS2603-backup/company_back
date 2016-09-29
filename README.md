# Tabla de contenidos
-  [Introducción](#introducción)
-  [API](#api-de-la-aplicación-model)
  - [Recurso Company](#recurso-company)
    - [GET /companys](#GET-/companys)
    - [GET /companys/{id}](#GET-/companys/{id})
    - [POST /companys](#POST-/companys)
    - [PUT /companys/{id}](#PUT-/companys/{id})
    - [DELETE /companys/{id}](#DELETE-/companys/{id})
  - [Recurso Employee](#recurso-employee)
    - [GET /employees](#GET-/employees)
    - [GET /employees/{id}](#GET-/employees/{id})
    - [POST /employees](#POST-/employees)
    - [PUT /employees/{id}](#PUT-/employees/{id})
    - [DELETE /employees/{id}](#DELETE-/employees/{id})

# API Rest
## Introducción
La comunicación entre cliente y servidor se realiza intercambiando objetos JSON. Para cada entidad se hace un mapeo a JSON, donde cada uno de sus atributos se transforma en una propiedad de un objeto JSON. Todos los servicios se generan en la URL /model.api/api/. Por defecto, todas las entidades tienen un atributo `id`, con el cual se identifica cada registro:

```javascript
{
    id: '',
    attribute_1: '',
    attribute_2: '',
    ...
    attribute_n: ''
}
```

Cuando se transmite información sobre un registro específico, se realiza enviando un objeto con la estructura mencionada en la sección anterior.
La única excepción se presenta al solicitar al servidor una lista de los registros en la base de datos, que incluye información adicional para manejar paginación de lado del servidor en el header `X-Total-Count` y los registros se envían en el cuerpo del mensaje como un arreglo.

La respuesta del servidor al solicitar una colección presenta el siguiente formato:

```javascript
[{}, {}, {}, {}, {}, {}]
```

## API de la aplicación model
### Recurso Company
El objeto Company tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    name: '' /*Tipo String*/,
    id: '' /*Tipo Long*/
}
```


#### Representación Full
```javascript
{
    // todo lo de la representación Basic más la collección de los objetos de relaciones composite.
    department: [{
    name: '' /*Tipo String*/,
    id: '' /*Tipo Long*/    }]
}
```


#### GET /companys

Retorna una colección de objetos Company en representación Basic.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Basic](#recurso-company)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /companys/{id}

Retorna una colección de objetos Company en representación Full.
Cada Company en la colección tiene embebidos los siguientes objetos con relaciones composite: Department.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Company a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Company en [representaciones Full](#recurso-company)
404|No existe un objeto Company con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /companys

Es el encargado de crear objetos Company.
Dado que Department es una clase hija de Company a través de una relación composite, este servicio también permite la creación de department asociados con un Company.
.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Company que será creado|Sí|[Representación Full](#recurso-company)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Company ha sido creado|[Representación Full](#recurso-company)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Company|Mensaje de error

#### PUT /companys/{id}

Es el encargado de actualizar objetos Company.
Dado que Department es una clase hija de Company a través de una relación composite, este servicio también permite la actualización de department asociados con un Company.
.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Company a actualizar|Sí|Integer
body|body|Objeto Company nuevo|Sí|[Representación Full](#recurso-company)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Company actualizado|[Representación Full](#recurso-company)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Company|Mensaje de error

#### DELETE /companys/{id}

Elimina un objeto Company.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Company a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error


[Volver arriba](#tabla-de-contenidos)
### Recurso Employee
El objeto Employee tiene 2 representaciones JSON:	

#### Representación Minimum
```javascript
{
    name: '' /*Tipo String*/,
    salary: '' /*Tipo Double*/,
    id: '' /*Tipo Long*/
}
```

#### Representación Basic
```javascript
{
    // todo lo de la representación Minimum más los objetos Minimum con relación simple.
    department: {
    name: '' /*Tipo String*/,
    id: '' /*Tipo Long*/    }
}
```



#### GET /employees

Retorna una colección de objetos Employee en representación Basic.
Cada Employee en la colección tiene embebidos los siguientes objetos: Department.

#### Parámetros

#### N/A

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Colección de [representaciones Basic](#recurso-employee)
409|Un objeto relacionado no existe|Mensaje de error
500|Error interno|Mensaje de error

#### GET /employees/{id}

Retorna una colección de objetos Employee en representación Full.
Cada Employee en la colección tiene los siguientes objetos: Department.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Employee a consultar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
200|OK|Objeto Employee en [representaciones Full](#recurso-employee)
404|No existe un objeto Employee con el ID solicitado|Mensaje de error
500|Error interno|Mensaje de error

#### POST /employees

Es el encargado de crear objetos Employee.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
body|body|Objeto Employee que será creado|Sí|[Representación Full](#recurso-employee)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Employee ha sido creado|[Representación Full](#recurso-employee)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo crear el objeto Employee|Mensaje de error

#### PUT /employees/{id}

Es el encargado de actualizar objetos Employee.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Employee a actualizar|Sí|Integer
body|body|Objeto Employee nuevo|Sí|[Representación Full](#recurso-employee)

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
201|El objeto Employee actualizado|[Representación Full](#recurso-employee)
409|Un objeto relacionado no existe|Mensaje de error
500|No se pudo actualizar el objeto Employee|Mensaje de error

#### DELETE /employees/{id}

Elimina un objeto Employee.

#### Parámetros

Nombre|Ubicación|Descripción|Requerido|Esquema
:--|:--|:--|:--|:--
id|Path|ID del objeto Employee a eliminar|Sí|Integer

#### Respuesta

Código|Descripción|Cuerpo
:--|:--|:--
204|Objeto eliminado|N/A
500|Error interno|Mensaje de error


[Volver arriba](#tabla-de-contenidos)
