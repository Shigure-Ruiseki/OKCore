package ruiseki.okcore.inventory.search;

import java.util.ArrayList;
import java.util.List;

public class SearchParser {

    private SearchParser() {}

    public static SearchNode parse(String input) {
        if (input == null || input.isEmpty()) return null;

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) return null;

        List<SearchNode> orNodes = new ArrayList<>();

        String lowInput = trimmedInput.toLowerCase();

        String[] orGroups = lowInput.split("\\|");

        for (String group : orGroups) {
            group = group.trim();
            if (group.isEmpty()) continue;

            List<SearchNode> andNodes = new ArrayList<>();
            List<String> terms = splitTerms(group);

            for (String raw : terms) {
                if (raw.isEmpty()) continue;

                SearchNode node;
                if (raw.length() > 1 && raw.charAt(0) == '-') {
                    node = parseTerm(raw.substring(1));
                    if (node != null) node = new NotNode(node);
                } else {
                    node = parseTerm(raw);
                }

                if (node != null) andNodes.add(node);
            }

            if (!andNodes.isEmpty()) {
                orNodes.add(andNodes.size() == 1 ? andNodes.getFirst() : new AndNode(andNodes));
            }
        }

        if (orNodes.isEmpty()) return null;
        return orNodes.size() == 1 ? orNodes.getFirst() : new OrNode(orNodes);
    }

    private static SearchNode parseTerm(String term) {
        if (term.isEmpty()) return null;

        char prefix = term.charAt(0);
        if (term.length() == 1) return new TextNode(term);
        String body = term.substring(1);
        return switch (prefix) {
            case '@' -> new ModNode(body);
            case '$' -> new OreNode(body);
            case '%' -> new CreativeTabNode(body);
            default -> new TextNode(term);
        };
    }

    private static List<String> splitTerms(String s) {
        List<String> result = new ArrayList<>();
        int len = s.length();
        boolean inQuote = false;
        int start = 0;

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\"') {
                inQuote = !inQuote;
            } else if (c == ' ' && !inQuote) {
                if (i > start) {
                    addTerm(result, s.substring(start, i));
                }
                start = i + 1;
            }
        }

        if (start < len) {
            addTerm(result, s.substring(start));
        }

        return result;
    }

    private static void addTerm(List<String> list, String term) {
        term = term.replace("\"", "")
            .trim();
        if (!term.isEmpty()) {
            list.add(term);
        }
    }
}
