//package br.com.cursos.screenmatch.principal;
//
//import br.com.cursos.screenmatch.model.DadosEpisodio;
//import br.com.cursos.screenmatch.model.DadosSerie;
//import br.com.cursos.screenmatch.model.DadosTemporada;
//import br.com.cursos.screenmatch.model.Episodio;
//import br.com.cursos.screenmatch.service.ConsumoApi;
//import br.com.cursos.screenmatch.service.ConverteDados;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class Principal {
//    private Scanner leitura = new Scanner(System.in);
//    private ConsumoApi consumo = new ConsumoApi();
//    private ConverteDados conversor = new ConverteDados();
//
//    private final String ENDERECO = "https://omdbapi.com/?t=";
//    private final String API_KEY = "&apikey=4535ba51";
//
//    public void exibeMenu(){
//
//        System.out.println("Digite o nome da Série para busca: ");
//        var nomeSerie = leitura.nextLine();
//        var consumoApi = new ConsumoApi();
//        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
//        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//        System.out.println(dados);
//
//        List<DadosTemporada> temporadas = new ArrayList<>();
//
//		for (int i =1; i<=dados.totalTemporadas(); i++){
//			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+")+ "&season=" + i + API_KEY);
//			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
//			temporadas.add(dadosTemporada);
//		}
//		temporadas.forEach(System.out::println);

//        for (int i = 0; i<dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo() + " " + episodiosTemporada.get(j).avaliacao());
//            }
//        }
//
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo() + " " + e.avaliacao())));
//        // temporadas.forEach(System.out::println);
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//                //.toList();
//
//        System.out.println("\n Top 5 episódios: ");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);
//
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodio(t.numero(), d))
//                ).collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
//        System.out.println("Digite um trecho do Título do episódio: ");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                        .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }
//
//        System.out.println("A partir de que ano você deseja ver os episodios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: "+e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data Lançamento: " + e.getDataLancamento().format(formatador)
//                ));
//
//        Map<Integer, Double> avaliacoesporTemporada = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//        System.out.println(avaliacoesporTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println("Estátisticas da série: \n");
//
//        System.out.println("Média dos episódios: " + est.getAverage());
//        System.out.println("Melhor episódio: " + est.getMax());
//        System.out.println("Pior episódio: " + est.getMin());
//        System.out.println("Quantidade de episódios avaliados: " + est.getCount());
//
//    }
//}

package br.com.cursos.screenmatch.principal;

import br.com.cursos.screenmatch.model.DadosSerie;
import br.com.cursos.screenmatch.model.DadosTemporada;
import br.com.cursos.screenmatch.model.Episodio;
import br.com.cursos.screenmatch.model.Serie;
import br.com.cursos.screenmatch.repository.SerieRepository;
import br.com.cursos.screenmatch.service.ConsumoApi;
import br.com.cursos.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        while (true) {
            var menu = """
                    ****** Menu de Opções ******
                    1 - Buscar Série
                    2 - Buscar Episódios
                    3 - Filtrar Episódios por Ano
                    4 - Estatísticas da Série
                    5 - Listar Séries buscadas 
                    0 - Sair
                    ****************************
                    """;

            System.out.println(menu);
            System.out.print("Selecione uma opção: ");
            var opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> filtrarEpisodiosPorAno();
                case 4 -> estatisticasSerie();
                case 5 -> listarSeriesBuscadas();
                case 0 -> {
                    System.out.println("Programa finalizado!!");
                    System.out.println("Obrigado e até mais !!");
                    return;
                }
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

    private void filtrarEpisodiosPorAno() {
        System.out.print("Digite o nome da série: ");
        DadosSerie dadosSerie = getDadosSerie();

        System.out.print("A partir de que ano deseja ver os episódios? ");
        int ano = leitura.nextInt();
        leitura.nextLine();
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Episodio> episodios = buscarTodosEpisodios(dadosSerie);

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " | Episódio: " + e.getTitulo() +
                                " | Data de Lançamento: " + e.getDataLancamento().format(formatador)
                ));
    }

    private void estatisticasSerie() {
        System.out.print("Digite o nome da série: ");
        DadosSerie dadosSerie = getDadosSerie();
        List<Episodio> episodios = buscarTodosEpisodios(dadosSerie);

        DoubleSummaryStatistics estatisticas = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("\nEstatísticas da Série: ");
        System.out.println("Média das avaliações: " + estatisticas.getAverage());
        System.out.println("Melhor episódio: " + estatisticas.getMax());
        System.out.println("Pior episódio: " + estatisticas.getMin());
        System.out.println("Quantidade de episódios avaliados: " + estatisticas.getCount());
    }

    private List<Episodio> buscarTodosEpisodios(DadosSerie dadosSerie) {
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        return temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());
    }

    private void listarSeriesBuscadas(){

        List<Serie> series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}
