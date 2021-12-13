WITH Src AS (
	SELECT medicine_id, rsch.research_code, registered
	FROM medicine
	inner join medicine_research as mdc_research on mdc_research.medicine_code = medicine.medicine_id
	inner join research as rsch on rsch.research_code = mdc_research.research_code
)

Select medicine_id ИД, research_code Исследование, registered Зарегестрировано
from Src
group by ИД