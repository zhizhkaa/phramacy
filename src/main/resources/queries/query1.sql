WITH Src AS
(
	SELECT
		emp.employee_id, emp.surname, emp.name,
        mw.serial,
        wo.warehouse_operation_id
	FROM warehouse_operations AS wo
    INNER JOIN employees AS emp ON emp.employee_Id = wo.storekeeper_id
    INNER JOIN medicine_warehouse AS mw ON mw.warehouse_operation_id = wo.warehouse_operation_id
)

SELECT
	employee_id ИД, surname Фамилия, name Имя,
    COUNT(warehouse_operation_id) Итого,
    GENERATE_PIVOT
FROM Src
GROUP BY ИД, Фамилия, Имя