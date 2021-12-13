WITH Src AS
(
	SELECT
		wks.workshop_number, prod.serial

	FROM workshops AS wks
    INNER JOIN medicine_batch AS batch ON wks.workshop_number = batch.workshop_number
    INNER JOIN medicine_production AS prod ON prod.serial = batch.serial
)

SELECT
	workshop_number ЦЕХ,
	GENERATE_PIVOT
FROM Src
GROUP BY ЦЕХ