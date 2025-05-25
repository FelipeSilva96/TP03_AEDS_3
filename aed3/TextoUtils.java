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
    public static Map<String,Integer> termFrequencies(String texto) {
        Map<String,Integer> freqs = new HashMap<>();
        for (String w : tokenize(texto)) {
            freqs.put(w, freqs.getOrDefault(w, 0) + 1);
        }
        return freqs;
    }
}
