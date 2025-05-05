package br.com.cursos.screenmatch.principal;

import br.com.cursos.screenmatch.model.*;
import br.com.cursos.screenmatch.repository.SerieRepository;
import br.com.cursos.screenmatch.service.ConsumoApi;
import br.com.cursos.screenmatch.service.ConverteDados;

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

//    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        while (true) {
            var menu = """
                    ****** Menu de Opções ******
                    1 -  Buscar Série
                    2 -  Listar Série por trecho do Título
                    3 -  Buscar Episódios
                    4 -  Filtrar Episódios por Ano
                    5 -  Estatísticas da Série
                    6 -  Listar Séries buscadas
                    7 -  Buscar série por ator
                    8 -  Top 5 séries
                    9 -  Buscar Série por categoria
                    10 - Filtrar séries
                    11 - Filtrar Sério por Trecho
                    12 - Top 5 Episódios de uma Série
                    0 -  Sair
                    ****************************
                    """;

            System.out.println(menu);
            System.out.print("Selecione uma opção: ");
            var opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarSerieTitulo();
                case 3 -> buscarEpisodioPorSerie();
                case 4 -> filtrarEpisodiosPorAno();
                case 5 -> estatisticasSerie();
                case 6 -> listarSeriesBuscadas();
                case 7 -> buscarSeriesPorAtor();
                case 8 -> buscarTop5Series();
                case 9 -> buscarSeriePorCategoria();
                case 10 -> filtrarSeriesPorTemporadaEAvaliacao();
                case 11 -> filtrarEpisodioPorTrecho();
                case 12 -> filtarTop5EpisodiosPorSerie();
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
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarSerieTitulo(){
        System.out.println("Escolha a série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!!!");
        }
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            Serie serieEscolhida = serieBusca.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEscolhida.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEscolhida.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEscolhida.setEpisodios(episodios);
            repositorio.save(serieEscolhida);
            System.out.println("Episódios salvos com sucesso no banco!");
        } else {
            System.out.println("Série não encontrada no banco!");
        }
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
                                " | Data Lançamento: " + e.getDataLancamento().format(formatador)
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
                        .map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());
    }

    private void listarSeriesBuscadas(){

        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

//    private void buscarSerieTitulo() {
//        System.out.print("Digite um trecho do título da série: ");
//        String trechoTitulo = leitura.nextLine();
//
//        List<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(trechoTitulo);
//
//        if (!seriesEncontradas.isEmpty()) {
//            System.out.println("\nSéries encontradas:");
//            seriesEncontradas.forEach(s -> System.out.println("- " + s.getTitulo()));
//        } else {
//            System.out.println("Nenhuma série encontrada com esse título.");
//        }
//    }

    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome do Ator/Atriz: ");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }
    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Deseja buscar série por qual categoria/genêro?");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPtBr(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries de categoria: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void filtrarEpisodioPorTrecho() {
        System.out.println("Qual o nome do Epsódio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporadas %s - Episódio %s - %s \n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void filtarTop5EpisodiosPorSerie(){
        buscarSerieTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.filtrarTopEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s | Temporada %s | Episódio %s | Titulo: %s | Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

}
