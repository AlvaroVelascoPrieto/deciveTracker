-- Database creation
CREATE DATABASE app_db;

USE app_db;

-- Table users creation
CREATE TABLE USUARIO(
    dni INTEGER PRIMARY KEY,
    contrasena VARCHAR(100),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    telefono VARCHAR(20),
    notificaciones VARCHAR(10),
    token VARCHAR(200)
);

