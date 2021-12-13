select act.name Вещество, supplier.name Поставщик, supplier.phone_number Телефон
from active_substance as act
inner join supplier on act.supplier_id = supplier.supplier_id