package br.com.cursos.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    AVENTURA("Adventure", "Aventura"),
    SUSPENSE("Suspense", "Suspense");

    private String categoriaOmdb;

    private String categoriasEmPt;

    Categoria(String categoriaOmdb, String categoriasEmPt){

        this.categoriaOmdb = categoriaOmdb;
        this.categoriasEmPt = categoriasEmPt;
    }

    public static Categoria fromString(String text) {
    for (Categoria categoria : Categoria.values()) {
        if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
            return categoria;
        }
    }
    throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);

}
    public static Categoria fromPtBr(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriasEmPt.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);

    }
}
