package ruiseki.okcore.client.gui.tooltip;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

public class TooltipBuilderTest {

    private List<String> build(TooltipBuilder builder) {
        List<String> result = new ArrayList<>();
        builder.build(null, null, result, false);
        return result;
    }

    @Test
    public void appendsInOrderAndPreservesExistingTooltip() {
        List<String> result = new ArrayList<>(Arrays.asList("existing"));
        TooltipBuilder builder = new TooltipBuilder();
        assertSame(builder, builder.addLine((s, p, lines, advanced) -> lines.add("first")));
        assertSame(builder, builder.addNewLine());
        builder.addLine((s, p, lines, advanced) -> lines.add("last"));
        builder.build(null, null, result, false);
        assertEquals(Arrays.asList("existing", "first", "", "last"), result);
    }

    @Test
    public void replacementUsesSubstringMatchAndCanEmitMultipleLines() {
        TooltipBuilder builder = new TooltipBuilder()
            .addLine((s, p, lines, a) -> lines.addAll(Arrays.asList("keep", "prefix old suffix", "old")));
        assertSame(builder, builder.replace("old", (s, p, lines, a) -> lines.addAll(Arrays.asList("new", "extra"))));
        assertEquals(Arrays.asList("keep", "new", "extra", "new", "extra"), build(builder));
    }

    @Test
    public void replacementDropsNullLinesAndCanRemoveMatches() {
        TooltipBuilder builder = new TooltipBuilder()
            .addLine((s, p, lines, a) -> lines.addAll(Arrays.asList(null, "remove", "keep")))
            .replace("remove", (s, p, lines, a) -> {});
        assertEquals(Arrays.asList("keep"), build(builder));
    }

    @Test
    public void replacementOnlyWrapsProvidersAlreadyPresent() {
        TooltipBuilder builder = new TooltipBuilder().addLine((s, p, lines, a) -> lines.add("old"))
            .replace("old", (s, p, lines, a) -> lines.add("new"))
            .addLine((s, p, lines, a) -> lines.add("old"));
        assertEquals(Arrays.asList("new", "old"), build(builder));
        assertEquals(Arrays.asList("new", "old"), build(builder));
    }

    @Test
    public void forwardsContextAndReevaluatesProvidersOnEachBuild() {
        TooltipBuilder builder = new TooltipBuilder().addLine((s, p, lines, advanced) -> {
            assertNull(s);
            assertNull(p);
            lines.add(Boolean.toString(advanced));
        })
            .replace("true", (s, p, lines, advanced) -> lines.add("advanced=" + advanced));
        List<String> result = new ArrayList<>();
        builder.build(null, null, result, true);
        assertEquals(Arrays.asList("advanced=true"), result);
        assertEquals(Arrays.asList("false"), build(builder));
    }

    @Test
    public void clearRemovesWrappedProvidersAndAllowsReuse() {
        TooltipBuilder builder = new TooltipBuilder().addNewLine()
            .replace(".*", (s, p, l, a) -> l.add("old"));
        assertSame(builder, builder.clear());
        assertTrue(build(builder).isEmpty());
        assertSame(builder, builder.addLines((String[]) null));
        builder.addNewLine();
        assertEquals(Arrays.asList(""), build(builder));
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullProvider() {
        new TooltipBuilder().addLine((TooltipBuilder.TooltipProvider) null);
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullReplacementEvenWhenEmpty() {
        new TooltipBuilder().replace("x", (TooltipBuilder.TooltipProvider) null);
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullRegex() {
        new TooltipBuilder().replace(null, (s, p, l, a) -> {});
    }

    @Test(expected = PatternSyntaxException.class)
    public void rejectsInvalidRegex() {
        new TooltipBuilder().replace("[", (s, p, l, a) -> {});
    }
}
