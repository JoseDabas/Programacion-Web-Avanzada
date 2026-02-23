package proyecto.util;

public enum DatosEstaticos {
    URL_MONGO("URL_MONGO"),
    DB_NOMBRE("DB_NOMBRE");

    private String valor;

    DatosEstaticos(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
