package name.fraser.neil.plaintext;

/*
 * Diff Match and Patch -- Test harness
 * Copyright 2018 The diff-match-patch Authors.
 * https://github.com/google/diff-match-patch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Compile from diff-match-patch/java with:
 * javac -d classes src/name/fraser/neil/plaintext/diff_match_patch.java tests/name/fraser/neil/plaintext/diff_match_patch_test.java
 * Execute with:
 * java -classpath classes name/fraser/neil/plaintext/diff_match_patch_test
 */

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.LinesToCharsResult;

public class word_diff_match_patch_test {

    private static word_diff_match_patch dmp;
    private static diff_match_patch.Operation DELETE = diff_match_patch.Operation.DELETE;
    private static diff_match_patch.Operation EQUAL = diff_match_patch.Operation.EQUAL;
    private static diff_match_patch.Operation INSERT = diff_match_patch.Operation.INSERT;


    private static String original = "# Test\n" +
            "\n" +
            "## Test 2\n" +
            "\n" +
            "This is not yet a heading\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed haec ab Antiocho, familiari nostro, dicuntur multo melius et fortius, quam a Stasea dicebantur. Et ille ridens: Video, inquit, quid agas; Quae in controversiam veniunt, de iis, si placet, disseramus. Quo studio Aristophanem putamus aetatem in litteris duxisse? Traditur, inquit, ab Epicuro ratio neglegendi doloris. Duo Reges: constructio interrete. Quae diligentissime contra Aristonem dicuntur a Chryippo. Quis istud possit, inquit, negare? Quacumque enim ingredimur, in aliqua historia vestigium ponimus.\n" +
            "\n" +
            "This is a deleted block.\n" +
            "\n" +
            "* Test\n" +
            "* Test 3";

    private static String revised = "# Test\n" +
            "\n" +
            "## Test 2\n" +
            "\n" +
            "## This is **now** a heading\n" +
            "\n" +
            "### Test 3\n" +
            "\n" +
            "Lorem *ipsum* dolor sit amet, consectetur adipiscing elit. Sed haec ab Antiocho, familiari nostro, dicuntur multo melius et fortius, quam a Stasea dicebantur. Et ille ridens: Video, inquit, quid agas; Quae in controversiam veniunt, de iis, si placet, disseramus. Quo studio Aristophanem putamus aetatem in litteris duxisse? Traditur, inquit, ab Epicuro ratio neglegendi doloris. Duo Reges: constructio interrete. This is a new sentence, inserted in the middle. Quae diligentissime contra Aristonem dicuntur a Chryippo. Quis istud possit, inquit, negare? Quacumque enim ingredimur, in aliqua historia vestigium ponimus.\n" +
            "\n" +
            "Something in the middle.\n" +
            "\n" +
            "This is a new block.\n" +
            "\n" +
            "Traditur, inquit, ab Epicuro ratio neglegendi doloris. Duo Reges: constructio interrete. This is a new sentence, inserted in the middle. Quae diligentissime contra Aristonem dicuntur a Chryippo. Quis istud possit, inquit, negare? Quacumque enim ingredimur, in aliqua historia vestigium ponimus.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed haec ab Antiocho, familiari nostro, dicuntur multo melius et fortius, quam a Stasea dicebantur. Et ille ridens: Video, inquit, quid agas; Quae in controversiam veniunt, de iis, si placet, disseramus. Quo studio Aristophanem putamus aetatem in litteris duxisse? Traditur, inquit, ab Epicuro ratio neglegendi doloris. Duo Reges: constructio interrete. This is a new sentence, inserted in the middle. Quae diligentissime contra Aristonem dicuntur a Chryippo. Quis istud possit, inquit, negare? Quacumque enim ingredimur, in aliqua historia vestigium ponimus.\n" +
            "\n" +
            "* Test\n" +
            "* Test 3\n" +
            "* Test 4\n" +
            "\n";


    //  DIFF TEST FUNCTIONS


    public static void testWordBasedDiff() {

        LinkedList<Diff> wordBasedDiff = dmp.diff_word_based("Markdown", "Markup");
        assertEquals("should have two edits", 2, wordBasedDiff.size());
        assertEquals("should delete whole word", "Markdown", wordBasedDiff.get(0).text);
        assertEquals("should insert whole word", "Markup", wordBasedDiff.get(1).text);

        LinkedList<Diff> diffs = dmp.diff_word_based(original, revised);

        diffs.stream().forEach(diff -> {
            switch(diff.operation) {
                case INSERT:
                    System.out.print(getInsertedText(diff.text));
                    break;
                case EQUAL:
                    System.out.print(diff.text);
                case DELETE:
                default:
                    break;
            }
        });

    }

    private static String getInsertedText(String text) {
        if (text.endsWith("\n")) {
            if (text.length() > 1) {
                return "{+" + text.substring(0, text.length() - 1) + "+}\n";
            } else {
                return "\n";
            }
        } else {
            return "{+" + text + "+}";
        }
    }

    private static void assertEquals(String error_msg, Object a, Object b) {
        if (!a.toString().equals(b.toString())) {
            throw new Error("assertEquals fail:\n Expected: " + a + "\n Actual: " + b
                    + "\n" + error_msg);
        }
    }

    private static void assertTrue(String error_msg, boolean a) {
        if (!a) {
            throw new Error("assertTrue fail: " + error_msg);
        }
    }

    private static void assertNull(String error_msg, Object n) {
        if (n != null) {
            throw new Error("assertNull fail: " + error_msg);
        }
    }

    private static void fail(String error_msg) {
        throw new Error("Fail: " + error_msg);
    }

    private static void assertArrayEquals(String error_msg, Object[] a, Object[] b) {
        List<Object> list_a = Arrays.asList(a);
        List<Object> list_b = Arrays.asList(b);
        assertEquals(error_msg, list_a, list_b);
    }

    private static void assertLinesToCharsResultEquals(String error_msg,
                                                       LinesToCharsResult a, LinesToCharsResult b) {
        assertEquals(error_msg, a.chars1, b.chars1);
        assertEquals(error_msg, a.chars2, b.chars2);
        assertEquals(error_msg, a.lineArray, b.lineArray);
    }

    // Construct the two texts which made up the diff originally.
    private static String[] diff_rebuildtexts(LinkedList<Diff> diffs) {
        String[] text = {"", ""};
        for (Diff myDiff : diffs) {
            if (myDiff.operation != diff_match_patch.Operation.INSERT) {
                text[0] += myDiff.text;
            }
            if (myDiff.operation != diff_match_patch.Operation.DELETE) {
                text[1] += myDiff.text;
            }
        }
        return text;
    }

    // Private function for quickly building lists of diffs.
    private static LinkedList<Diff> diffList(Diff... diffs) {
        LinkedList<Diff> myDiffList = new LinkedList<Diff>();
        for (Diff myDiff : diffs) {
            myDiffList.add(myDiff);
        }
        return myDiffList;
    }

    public static void main(String args[]) {
        dmp = new word_diff_match_patch();

        testWordBasedDiff();


        System.out.println("All tests passed.");
    }
}
