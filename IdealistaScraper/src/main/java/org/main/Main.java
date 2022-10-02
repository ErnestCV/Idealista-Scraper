package org.main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    private static final int ELEMENTS_PER_PAGE = 30;

    public static void main(String[] args) {

        //URL de la localització
        String url = "https://www.idealista.com/ca/alquiler-viviendas/barcelona/eixample/el-fort-pienc/";
        HtmlPage page = getDocument(url);

        //Obtenim el nombre d'habitatges disponibles a la zona
        String numberSelector = "//h1[@id=\"h1-container\"]/text()";
        List<Object> totalTemp = page.getByXPath(numberSelector);
        double total = objectToDouble(totalTemp.get(0));

        //Obtenim el llistat de preus
        List<Double> prices = new ArrayList<>();
        String priceSelector = "//span[@class=\"item-price h2-simulated\"]/text()";

        //Trobem els preus de la primera pàgina
        List<Object> firstPageElements = page.getByXPath(priceSelector);
        firstPageElements.forEach(price ->
                prices.add(objectToDouble(price)));

        //Iterem per totes les altres pàgines començant per la segona
        for (int i = 2; i <= Math.ceil(total / ELEMENTS_PER_PAGE); i++) {
            String formattedUrl = String.format(url + "pagina-%d.htm", i);
            HtmlPage tempPage = getDocument(formattedUrl);
            List<Object> currentPageElements = tempPage.getByXPath(priceSelector);
            currentPageElements.forEach(price ->
                    prices.add(objectToDouble(price)));
        }

        //Comprovem el nombre d'habitatges
        System.out.println("Nombre d'habitatges: " + prices.size());

        //Mitjana de preus
        double average = prices.stream().mapToDouble(a -> a).average().orElse(0);
        NumberFormat formatter = new DecimalFormat("#0.00");
        System.out.println("Mitjana de preus: " + formatter.format(average) + " euros");

        //Mediana de preus
        Collections.sort(prices);
        System.out.println("Mediana de preus: " + percentile(prices, 50) + " euros");
    }

    public static HtmlPage getDocument(String url) {
        HtmlPage page = null;
        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX)) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            page = webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    //Obtenim preu en double a partir de les dades retornades
    public static double objectToDouble(Object price) {
        return Double.parseDouble(price.toString().split(" ")[0].replace(".", ""));
    }

    //Percentils
    public static Double percentile(List<Double> prices, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * prices.size());
        return prices.get(index - 1);
    }
}