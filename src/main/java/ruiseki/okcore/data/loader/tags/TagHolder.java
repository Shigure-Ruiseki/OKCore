package ruiseki.okcore.data.loader.tags;

import java.util.List;

import ruiseki.okcore.tag.entry.TagEntry;

public record TagHolder<T> (boolean replace, List<TagEntry<T>> values) {

}
