package ru.hehmdalolkek.shop;

import org.springframework.boot.SpringApplication;

public class TestShopApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShopApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
