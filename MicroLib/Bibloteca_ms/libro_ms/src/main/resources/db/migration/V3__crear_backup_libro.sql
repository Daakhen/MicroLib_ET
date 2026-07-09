CREATE TABLE IF NOT EXISTS libro_backup (
    id BIGINT NOT NULL,
    titulo VARCHAR(255),
    autor VARCHAR(255),
    isbn VARCHAR(100),
    disponible BOOLEAN,
    fecha_backup TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //

DROP TRIGGER IF EXISTS despues_insert_libro//

CREATE TRIGGER despues_insert_libro
AFTER INSERT ON libro
FOR EACH ROW
BEGIN
    INSERT INTO libro_backup (
        id,
        titulo,
        autor,
        isbn,
        disponible
    )
    VALUES (
        NEW.id,
        NEW.titulo,
        NEW.autor,
        NEW.isbn,
        NEW.disponible
    );
END//

DELIMITER ;