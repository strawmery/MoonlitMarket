# MoonlitMarket

## 📌 Descripción del Proyecto

Este proyecto es una aplicación de comercio electrónico desarrollada en Java con MySQL, diseñada específicamente para una tienda vintage. El sistema permite gestionar productos, usuarios, pedidos y un sistema de lista de deseos, proporcionando una plataforma completa para la venta en línea de artículos vintage.

## 🛠 Tecnologías Utilizadas

- Java (versión 8 o superior)
- MySQL (versión 5.7 o superior)
- JDBC para la conexión con la base de datos
- Maven (opcional, para gestión de dependencias)

## 📋 Características Principales

1. **Gestión de Productos**
   - Catálogo de productos vintage
   - Categorización de productos
   - Gestión de inventario
   - Precios y descripciones detalladas

2. **Sistema de Usuarios**
   - Registro y autenticación
   - Perfiles de usuario
   - Roles y permisos (Administrador, Cliente)
   - Gestión de información personal

3. **Gestión de Pedidos**
   - Creación y seguimiento de pedidos
   - Estado de pedidos
   - Historial de compras

4. **Sistema de Facturación**
   - Generación automática de facturas
   - Cálculo de impuestos
   - Registro de transacciones

5. **Lista de Deseos**
   - Guardar productos favoritos
   - Gestión de lista de deseos personalizada

## 🗄️ Estructura de la Base de Datos

![Diagrama Entidad-Relación del Proyecto]()

## 🚀 Requisitos Previos e Instalación

### Requisitos Previos

1. **Java Development Kit (JDK) 8 o superior**
   - Descarga: [Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) o [OpenJDK](https://adoptopenjdk.net/)
   - Verifica la instalación: `java -version`

2. **MySQL 5.7 o superior**
   - Descarga: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Verifica la instalación: `mysql --version`

3. **IDE Java** (recomendado)
   - Opciones: [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), [Eclipse](https://www.eclipse.org/downloads/), o [NetBeans](https://netbeans.apache.org/download/index.html)

4. **Maven** (opcional, para gestión de dependencias)
   - Descarga: [Apache Maven](https://maven.apache.org/download.cgi)
   - Verifica la instalación: `mvn -version`

### Instalación y Configuración

1. **Clonar el Repositorio**
   ```bash
   git clone https://github.com/tu-usuario/vintage-store-ecommerce.git
   cd vintage-store-ecommerce

2. **Configuración de la Base de Datos**
   ```bash
   CREATE DATABASE moonlitmarket;
   USE moonlitmarket;
   
3.**Configuración del Proyecto**
  -Abrir el proyecto en tu IDE preferido
  Configurar las credenciales de la base de datos en el archivo `src/main/resources/config.properties`:
    ```bash
      db.url=jdbc:mysql://localhost:3306/vintage_store
      db.user=tu_usuario
      db.password=tu_contraseña;

5.**Compilar el Proyecto**

- Con Maven: `mvn clean install`
- Sin Maven: Compilar usando tu IDE
- java -jar target/vintage-store-ecommerce-1.0-SNAPSHOT.jar

  ## 🤝 Contribución

Si deseas contribuir al proyecto:

1. Fork del repositorio
2. Crear una rama para tu característica (`git checkout -b feature/AmazingFeature`)
3. Commit de tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request
