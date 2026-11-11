
package br.com.cidadaoconecta.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexao JDBC com o Oracle Database.
 *
 * Projeto: CidadaoConecta
 * Global Solution FIAP 2026/2
 */
public class ConexaoOracle {

    private ConexaoOracle() {
        // Impede a criacao de instancias.
    }

    public static Connection conectar() throws SQLException {

        String url = System.getenv("CC_DB_URL");
        String usuario = System.getenv("CC_DB_USER");
        String senha = System.getenv("CC_DB_PASSWORD");

        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()
                || senha == null || senha.isBlank()) {

            throw new SQLException(
                "Configuracao incompleta. " +
                "Verifique CC_DB_URL, CC_DB_USER " +
                "e CC_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(
            url, usuario, senha
        );
    }
}