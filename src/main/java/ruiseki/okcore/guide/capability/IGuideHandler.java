package ruiseki.okcore.guide.capability;

import java.util.Set;

public interface IGuideHandler {

    void discoverBook(String bookId);

    boolean hasDiscovered(String bookId);

    Set<String> getDiscoveredBooks();

    void setLastPos(String entry, int category, int page);

    String getLastEntry();

    int getLastCategory();

    int getLastPage();
}
