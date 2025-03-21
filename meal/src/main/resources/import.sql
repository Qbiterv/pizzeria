INSERT INTO meal_category (name) VALUES ('pizza'), ('makaron'), ('napój');

INSERT INTO meal (name, description, category_id) VALUES ('Margherita', '', (SELECT id FROM meal_category WHERE name = 'pizza'));
INSERT INTO meal (name, description, category_id) VALUES ('Capriciosa', '', (SELECT id FROM meal_category WHERE name = 'pizza'));
INSERT INTO meal (name, description, category_id) VALUES ('Carbonara', '', (SELECT id FROM meal_category WHERE name = 'makaron'));
INSERT INTO meal (name, description, category_id) VALUES ('Bolognese', '', (SELECT id FROM meal_category WHERE name = 'makaron'));
INSERT INTO meal (name, description, category_id) VALUES ('Cola', '', (SELECT id FROM meal_category WHERE name = 'napój'));
INSERT INTO meal (name, description, category_id) VALUES ('Woda', '', (SELECT id FROM meal_category WHERE name = 'napój'));

INSERT INTO category (name) VALUES ('danie'), ('napój'), ('zestaw'), ('bestseller');

-- produkty
INSERT INTO product (name, description, price) VALUES ('Carbonara', '', 30);
INSERT INTO product (name, description, price) VALUES ('Margherita', '', 20);
INSERT INTO product (name, description, price) VALUES ('Woda', '', 3);
INSERT INTO product (name, description, price) VALUES ('Capriciosa', '', 25);
INSERT INTO product (name, description, price) VALUES ('Cola', '', 5);
INSERT INTO product (name, description, price) VALUES ('Bolognese', '', 35);
-- zestawy
INSERT INTO product (name, description, price) VALUES ('zestaw seby', 'super zestaw seby', 52);
INSERT INTO product (name, description, price) VALUES ('zestaw marka', 'super zestaw marka', 55);

--kategorie produktow
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'danie'), (SELECT id FROM product WHERE name = 'Margherita'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'danie'), (SELECT id FROM product WHERE name = 'Capriciosa'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'danie'), (SELECT id FROM product WHERE name = 'Carbonara'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'danie'), (SELECT id FROM product WHERE name = 'Bolognese'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'napój'), (SELECT id FROM product WHERE name = 'Cola'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'napój'), (SELECT id FROM product WHERE name = 'Woda'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'zestaw'), (SELECT id FROM product WHERE name = 'zestaw seby'));
INSERT INTO product_category (category_id, product_id) VALUES ((SELECT id FROM category WHERE name = 'zestaw'), (SELECT id FROM product WHERE name = 'zestaw marka'));

--produkty z mealem
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'Margherita'), (SELECT id FROM meal WHERE name = 'Margherita'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'Capriciosa'), (SELECT id FROM meal WHERE name = 'Capriciosa'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'Carbonara'), (SELECT id FROM meal WHERE name = 'Carbonara'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'Bolognese'), (SELECT id FROM meal WHERE name = 'Bolognese'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product where name = 'Cola'), (SELECT id FROM meal WHERE name = 'Cola'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'Woda'), (SELECT id FROM meal WHERE name = 'Woda'));
--zestawy z mealami
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw seby'), (SELECT id FROM meal WHERE name = 'Bolognese'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw seby'), (SELECT id FROM meal WHERE name = 'Cola'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw seby'), (SELECT id FROM meal WHERE name = 'Woda'));

INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Margherita'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Woda'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Capriciosa'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Cola'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Capriciosa'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Carbonara'));
INSERT INTO product_meal (product_id, meal_id) VALUES ((SELECT id FROM product WHERE name = 'zestaw marka'), (SELECT id FROM meal WHERE name = 'Capriciosa'));