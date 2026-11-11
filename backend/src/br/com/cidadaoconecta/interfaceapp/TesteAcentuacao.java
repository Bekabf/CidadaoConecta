
package br.com.cidadaoconecta.interfaceapp;

import java.nio.charset.Charset;
import java.util.Scanner;

public class TesteAcentuacao {

    public static void main(String[] args) {

        // Testa a leitura de caracteres usando Windows-1252
        Charset codificacao = java.nio.charset.StandardCharsets.UTF_8;

        Scanner scanner = new Scanner(
            System.in,
            codificacao
        );

        System.out.println("===================================");
        System.out.println(" CIDADAOCONECTA - TESTE DE ACENTOS ");
        System.out.println("===================================");

        System.out.print(
            "\nDigite: Iluminação pública em Anápolis: "
        );

        String texto = scanner.nextLine();

        System.out.println("\nTexto recebido: " + texto);

        System.out.println("\nCodigos Unicode:");

        texto.codePoints().forEach(codigo -> {
            System.out.printf("U+%04X ", codigo);
        });

        System.out.println();

        // Verifica se houve perda de caracteres
        if (texto.contains("\uFFFD")) {
            System.out.println(
                "\nATENCAO: Foram encontrados caracteres invalidos."
            );
        } else {
            System.out.println(
                "\nNenhum caractere de substituicao encontrado."
            );
        }

        System.out.println("\nTeste finalizado.");

        // Nao acessa nem altera o banco Oracle.
        scanner.close();
    }
}