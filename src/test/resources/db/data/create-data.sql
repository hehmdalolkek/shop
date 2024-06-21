INSERT INTO public.products (product_id, title, price, active)
VALUES
    (1, 'Apple', 15.99, true),
    (2, 'Banana', 30, true),
    (3, 'Kiwi', 99.23, true),
    (4, 'Orange', 45.22, true),
    (5, 'Mango', 200.40, false);

SELECT setval('products_product_id_seq', (SELECT MAX(product_id) FROM products) + 1);