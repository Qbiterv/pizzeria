-- statuses
INSERT INTO status (state, name, type) VALUES (1, 'Zamówienie anulowane', 0);
INSERT INTO status (state, name, type) VALUES (2, 'Zamówienie złożone', 1);
INSERT INTO status (state, name, type) VALUES (3, 'Zamówienie w trakcie produkcji', 2);
INSERT INTO status (state, name, type) VALUES (3, 'Zamówienie wydane do doręczenia', 3);
INSERT INTO status (state, name, type) VALUES (4, 'Zamówienie w trakcie dostawy', 3);
INSERT INTO status (state, name, type) VALUES (5, 'Doręczono', 4);