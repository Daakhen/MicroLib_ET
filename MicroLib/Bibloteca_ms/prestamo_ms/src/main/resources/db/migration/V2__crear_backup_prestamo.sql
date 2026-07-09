CREATE TABLE IF NOT EXISTS prestamo_backup (
    libro_id BIGINT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_backup TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //

DROP TRIGGER IF EXISTS despues_insert_prestamo//

CREATE TRIGGER despues_insert_prestamo
AFTER INSERT ON prestamo
FOR EACH ROW
BEGIN
    INSERT INTO prestamo_backup (
        libro_id,
        fecha_prestamo,
        fecha_devolucion_real,
        estado
    )
    VALUES (
        NEW.libro_id,
        NEW.fecha_prestamo,
        NEW.fecha_devolucion_real,
        NEW.estado
    );
END//

DELIMITER ;