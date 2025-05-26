package aed3;

import java.util.*;
import java.util.regex.*;

public class TextoUtils {
    // Pequena lista de stop-words em PT-BR
    private static final Set<String> STOP_WORDS = Set.of(
        "de","da","do","das","dos","e","a","o","os","as","um","uma","para","com","por","em"
    );
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\p{L}+");

    /** Retorna a lista de tokens limpos e normalizados (minúsculos) */
    public static List<String> tokenize(String texto) {
        List<String> tokens = new ArrayList<>();
        Matcher m = TOKEN_PATTERN.matcher(texto.toLowerCase());
        while (m.find()) {
            String w = m.group();
            if (!STOP_WORDS.contains(w)) {
                tokens.add(w);
            }
        }
        return tokens;
    }

    /** Retorna mapa palavra→frequência no texto */
    public static Map<String, Double> termFrequencies(String texto) {
        Map<String,Double> freqs = new HashMap<>();
        List<String> tokens = tokenize(texto);
        Double reg_freq = (double) (1.0/tokens.size());
        for (String w : tokens) {
            if(freqs.get(w)==null){
                freqs.put(w, reg_freq);
            }else{
                freqs.put(w, freqs.get(w)+reg_freq);
            }
            
        }
        return freqs;
    }

    public void printMap(Map<String, Double> map){
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key + " ; " + value);
        }
    }
}
