
package br.com.cidadaoconecta.interfaceapp;

import br.com.cidadaoconecta.config.ConexaoOracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Testa a conexao JDBC com o Oracle da FIAP.
 * Executa somente uma consulta SELECT.
 *
 * Global Solution FIAP 2026/2
 */
public class TesteConexao {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("CIDADÃOCONECTA - TESTE JDBC");
        System.out.println("==================================");

        String sql = "SELECT COUNT(*) AS TOTAL "
                   + "FROM CC2_CATEGORIA";

        try (Connection conexao = ConexaoOracle.conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            System.out.println("Conexao Oracle estabelecida!");

            if (resultado.next()) {
                System.out.println(
                    "Categorias cadastradas: "
                    + resultado.getInt("TOTAL")
                );
            }

            System.out.println("Teste concluido com sucesso!");

        } catch (SQLException erro) {
            System.err.println(
                "Nao foi possivel concluir o teste JDBC."
            );
            System.err.println(
                "Detalhes: " + erro.getMessage()
            );
        }
    }
}