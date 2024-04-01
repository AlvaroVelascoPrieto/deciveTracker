-- Database creation
CREATE DATABASE app_db;

USE app_db;

-- Table users creation
CREATE TABLE USUARIO(
    dni INTEGER PRIMARY KEY,
    contrasena VARCHAR(100),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    telefono VARCHAR(20)
);

INSERT INTO USUARIO VALUES(123, 'alvaro123', 'Alvaro', 'Velasco', '312456987');
