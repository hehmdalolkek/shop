INSERT INTO public.products (product_id, title, price, active)
VALUES
    (1, 'Apple', 15.99, true),
    (2, 'Banana', 30, true),
    (3, 'Kiwi', 99.23, true),
    (4, 'Orange', 45.22, true),
    (5, 'Mango', 200.40, false);

SELECT setval('products_product_id_seq', (SELECT MAX(product_id) FROM products) + 1);

INSERT INTO public.orders (order_id, order_date, total_cost)
VALUES
    (1, '2024-10-06T10:20:10', 79.95),
    (2, '2024-10-07T12:30:50', 159.23),
    (3, '2024-10-08T13:40:20', 60);

SELECT setval('orders_order_id_seq', (SELECT MAX(order_id) FROM orders) + 1);

INSERT INTO public.orders_products (order_product_id, order_id, product_id, amount)
VALUES
    (1, 1, 1, 5),
    (2, 2, 2, 2),
    (3, 2, 1, 3),
    (4, 3, 2, 2);

SELECT setval('orders_products_order_product_id_seq', (SELECT MAX(order_product_id) FROM orders_products) + 1);