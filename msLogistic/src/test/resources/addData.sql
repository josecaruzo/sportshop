INSERT INTO purchases (id,customer_cpf, customer_name, delivery_address, delivery_group, delivery_zip_code, status, total_amount)
VALUES (1000000, '767.368.420-93', 'Nome test', 'Rua Pedro João Candiano nª 56, Jardim Myrian, Indaiatuba - São Paulo, Brasil', 1, '13074-000', 'AGUARDANDO PAGAMENTO', 599.8);

INSERT INTO purchases (id, customer_cpf, customer_name, delivery_address, delivery_group, delivery_zip_code, status, total_amount)
VALUES (1000001, '767.368.420-93', 'Nome test', 'Rua Pedro João Candiano nª 56, Jardim Myrian, Indaiatuba - São Paulo, Brasil', 1, '13074-000', 'PAGO', 599.8);

INSERT INTO purchases (id, customer_cpf, customer_name, delivery_address, delivery_group, delivery_zip_code, status, total_amount)
VALUES (1000002, '767.368.420-93', 'Nome test', 'Rua Pedro João Candiano nª 56, Jardim Myrian, Indaiatuba - São Paulo, Brasil', 1, '13074-000', 'PAGO', 599.8);

INSERT INTO purchases (id, customer_cpf, customer_name, delivery_address, delivery_group, delivery_zip_code, status, total_amount)
VALUES (1000003, '767.368.420-93', 'Nome test', 'Rua Pedro João Candiano nª 56, Jardim Myrian, Indaiatuba - São Paulo, Brasil', 1, '13074-000', 'AGUARDANDO ENTREGA', 599.8);

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000000, 1000000, 'AGUARDANDO PAGAMENTO', '2024-05-01 00:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000001, 1000001, 'AGUARDANDO PAGAMENTO', '2024-05-01 00:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000002, 1000001, 'PAGO', '2024-05-01 01:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000003, 1000002, 'AGUARDANDO PAGAMENTO', '2024-05-01 00:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000004, 1000002, 'PAGO', '2024-05-01 01:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000005, 1000003, 'AGUARDANDO PAGAMENTO', '2024-05-01 00:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000006, 1000003, 'PAGO', '2024-05-01 01:00:00');

INSERT INTO purchase_history (id, purchase_id, status, status_date)
VALUES (1000007, 1000003, 'AGUARDANDO ENTREGA', '2024-05-01 01:00:00');
