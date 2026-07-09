CREATE TABLE IF NOT EXISTS usuario_backup (
    id BIGINT NOT NULL,
    nombre VARCHAR(255),
    correo VARCHAR(255),
    fecha_backup TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //

DROP TRIGGER IF EXISTS despues_insert_usuario//

CREATE TRIGGER despues_insert_usuario
AFTER INSERT ON usuario
FOR EACH ROW
BEGIN
    INSERT INTO usuario_backup (
        id,
        nombre,
        correo
    )
    VALUES (
        NEW.id,
        NEW.nombre,
        NEW.correo
    );
END//

DELIMITER ;