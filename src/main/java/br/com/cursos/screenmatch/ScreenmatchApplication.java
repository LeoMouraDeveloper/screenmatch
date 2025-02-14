package br.com.cursos.screenmatch;

import br.com.cursos.screenmatch.model.DadosEpisodio;
import br.com.cursos.screenmatch.model.DadosSerie;
import br.com.cursos.screenmatch.model.DadosTemporada;
import br.com.cursos.screenmatch.service.ConsumoApi;
import br.com.cursos.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

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
		json = consumoApi.obterDados("https://omdbapi.com/?t=vikings&season=3&episode=6&apikey=4535ba51");
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i =1; i<=dados.totalTemporadas(); i++){
			json = consumoApi.obterDados("https://omdbapi.com/?t=vikings&season=" + i +"&apikey=4535ba51");
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);
	}
}
