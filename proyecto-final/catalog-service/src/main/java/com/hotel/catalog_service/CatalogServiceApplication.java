package com.hotel.catalog_service;

import com.hotel.catalog_service.model.Propiedad;
import com.hotel.catalog_service.repository.PropiedadRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@EnableDiscoveryClient // Permite a la instacia registrarse en el Eureka Server
@SpringBootApplication
public class CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}

	// Metodo que se ejecuta unicamente al levantar la aplicacion; puebla la base de datos si se encuentra vacia
	@Bean
	public CommandLineRunner initData(PropiedadRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				// Configuramos el Faker en español
				Faker faker = new Faker(Locale.of("es"));
				Random random = new Random();
				
				// Generamos un numero aleatorio entre 20 y 30 propiedades
				int cantidad = faker.number().numberBetween(20, 31);
				String[] tipos = {"Suite", "Sencilla", "Doble", "Presidencial", "Familiar"};

				for (int i = 0; i < cantidad; i++) {
					Propiedad p = new Propiedad(
							faker.company().name() + " Hotel",
							faker.lorem().paragraph(),
							List.of("WiFi", "Piscina", "Aire Acondicionado", "Desayuno Incluido"),
							List.of("https://loremflickr.com/640/480/hotel"),
							faker.number().randomDouble(2, 50, 500),
							faker.address().cityName(),
							tipos[random.nextInt(tipos.length)],
							"Hotel"
					);
					repository.save(p);
				}
				System.out.println("======> Catalogo inicializado con exito insertando: " + cantidad + " propiedades.");
			}
		};
	}
}
