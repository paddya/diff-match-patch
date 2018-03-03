package name.fraser.neil.plaintext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class word_diff_match_patch extends diff_match_patch {

    static public final String WITH_DELIMITER = "(?<=%1$s)";

    public LinkedList<Diff> diff_word_based(String text1, String text2) {
        LinesToCharsResult a = this.diff_linesToWords(text1, text2);
        String lineText1 = a.chars1;
        String lineText2 = a.chars2;
        List<String> lineArray = a.lineArray;
        LinkedList<Diff> diffs = this.diff_main(lineText1, lineText2, true);
        this.diff_cleanupSemantic(diffs);
        this.diff_charsToLines(diffs, lineArray);

        //this.diff_cleanupSemantic(diffs);

        this.diff_avoidWraps(diffs);


        return diffs;
    }

    protected LinesToCharsResult diff_linesToWords(String text1, String text2) {
        List<String> lineArray = new ArrayList<String>();
        Map<String, Integer> lineHash = new HashMap<String, Integer>();
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        //lineArray.add("");

        String chars1 = diff_wordsToCharsMunge(text1, lineArray, lineHash);
        String chars2 = diff_wordsToCharsMunge(text2, lineArray, lineHash);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    private String diff_wordsToCharsMunge(String text, List<String> lineArray,
                                          Map<String, Integer> lineHash) {
        int lineStart = 0;
        int lineEnd = -1;
        StringBuilder chars = new StringBuilder();
        // Walk the text, pulling out a substring for each line.
        // text.split('\n') would would temporarily double our memory footprint.
        // Modifying text would create many large strings to garbage collect.

        String[] words = text.split("(?<=\\s)|(?=\\s)");

        for (String word : words) {
            if (lineHash.containsKey(word)) {
                chars.append(String.valueOf((char) (int) lineHash.get(word)));
            } else {
                lineArray.add(word);
                lineHash.put(word, lineArray.size() - 1);
                chars.append(String.valueOf((char) (lineArray.size() - 1)));
            }
        }

//        while (lineEnd < text.length() - 1) {
//            lineEnd = indexOfNextWordEnd(text, lineStart);
//            if (lineEnd == -1) {
//                lineEnd = text.length() - 1;
//            }
//
//            line = text.substring(lineStart, lineEnd + 1);
//            lineStart = lineEnd + 1;
//
//            if (lineHash.containsKey(line)) {
//                chars.append(String.valueOf((char) (int) lineHash.get(line)));
//            } else {
//                lineArray.add(line);
//                lineHash.put(line, lineArray.size() - 1);
//                chars.append(String.valueOf((char) (lineArray.size() - 1)));
//            }
//        }
        return chars.toString();
    }

    private int indexOfNextWordEnd(String text, int lineStart) { ;
        String wordBoundaries = "\n:,;\" ";

        for (int i = lineStart ; i < text.length(); i++) {
            if (wordBoundaries.indexOf(text.charAt(i)) != -1) {
                return i;
            }
        }
        return -1;

    }

    private void diff_avoidWraps(List<Diff> diffs) {

        ListIterator<Diff> pointer = diffs.listIterator();

        while (pointer.hasNext()) {
            Diff diff = pointer.next();

            if (diff.text.contains("\n")) {
                String[] lines = diff.text.split(String.format(WITH_DELIMITER, "\n"));

                pointer.remove();
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];

                    Pattern trailingWhitespace = Pattern.compile("(\\s*)$");
                    Matcher matcher = trailingWhitespace.matcher(line);

                    String removedWhitespace = null;

                    if (matcher.find()) {
                        removedWhitespace = matcher.group(0);
                    }
                    line = line.replaceAll(trailingWhitespace.toString(), "");


                    if (line.length() > 0) {
                        pointer.add(new Diff(diff.operation, line));
                    }

                    if (removedWhitespace != null) {
                        pointer.add(new Diff(Operation.EQUAL, removedWhitespace));
                    }
                }

            }
        }
    }

}
