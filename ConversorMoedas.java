import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConversorMoedas {
    private static final String API_KEY = "880607915207a8f04f334497";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    // Mapa para armazenar as opções de moedas
    private static final Map<Integer, String[]> OPCOES_MOEDAS = new HashMap<>();
    static {
        OPCOES_MOEDAS.put(1, new String[] { "USD", "EUR" });
        OPCOES_MOEDAS.put(2, new String[] { "EUR", "USD" });
        OPCOES_MOEDAS.put(3, new String[] { "USD", "BRL" });
        OPCOES_MOEDAS.put(4, new String[] { "BRL", "USD" });
        OPCOES_MOEDAS.put(5, new String[] { "EUR", "BRL" });
        OPCOES_MOEDAS.put(6, new String[] { "BRL", "EUR" });
        OPCOES_MOEDAS.put(7, new String[] { "USD", "GBP" });
        OPCOES_MOEDAS.put(8, new String[] { "GBP", "USD" });
        OPCOES_MOEDAS.put(9, new String[] { "EUR", "JPY" });
        OPCOES_MOEDAS.put(10, new String[] { "JPY", "EUR" });
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int escolha = exibirMenu(scanner);
            if (!OPCOES_MOEDAS.containsKey(escolha)) {
                System.out.println("Opção inválida. Tente novamente.");
                continue;
            }

            String moedaOrigem = OPCOES_MOEDAS.get(escolha)[0];
            String moedaDestino = OPCOES_MOEDAS.get(escolha)[1];

            System.out.println("Convertendo de " + moedaOrigem + " para " + moedaDestino);

            System.out.print("Digite o valor em " + moedaOrigem + ": ");
            double valor = scanner.nextDouble();

            try {
                double taxa = obterTaxaConversao(moedaOrigem, moedaDestino);
                double valorConvertido = valor * taxa;
                System.out.printf("%.2f %s equivale a %.2f %s%n", valor, moedaOrigem, valorConvertido, moedaDestino);
            } catch (Exception e) {
                System.out.println("Erro ao obter a taxa de conversão: " + e.getMessage());
            }

            System.out.print("Deseja realizar outra conversão? (s/n): ");
            String continuar = scanner.next();
            if (!continuar.equalsIgnoreCase("s")) {
                break;
            }
        }
        scanner.close();
    }

    private static int exibirMenu(Scanner scanner) {
        System.out.println("Conversor de Moedas");
        System.out.println("Escolha uma opção de conversão:");

        for (int key : OPCOES_MOEDAS.keySet()) {
            String moedaOrigem = OPCOES_MOEDAS.get(key)[0];
            String moedaDestino = OPCOES_MOEDAS.get(key)[1];
            System.out.printf("%d. %s para %s%n", key, moedaOrigem, moedaDestino);
        }

        System.out.print("Digite o número da opção desejada: ");
        return scanner.nextInt();
    }

    private static double obterTaxaConversao(String moedaOrigem, String moedaDestino) throws Exception {
        String urlString = API_URL + moedaOrigem;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro HTTP: " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
        JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");
        return taxas.get(moedaDestino).getAsDouble();
    }
}