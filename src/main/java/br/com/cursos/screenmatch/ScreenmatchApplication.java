package br.com.cursos.screenmatch;

import br.com.cursos.screenmatch.model.DadosSerie;
import br.com.cursos.screenmatch.service.ConsumoApi;
import br.com.cursos.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();

		var json = consumoApi.obterDados("https://omdbapi.com/?t=vikings&apikey=4535ba51");
		System.out.println(json);
		//json = consumoApi.obterDados("");
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}
}
