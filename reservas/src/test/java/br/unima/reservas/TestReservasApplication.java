package br.unima.reservas;

import org.springframework.boot.SpringApplication;

public class TestReservasApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReservasApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
