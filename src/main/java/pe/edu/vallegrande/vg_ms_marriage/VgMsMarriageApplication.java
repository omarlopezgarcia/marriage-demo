package pe.edu.vallegrande.vg_ms_marriage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VgMsMarriageApplication {

	public static void main(String[] args) {
		SpringApplication.run(VgMsMarriageApplication.class, args);
	}

}
