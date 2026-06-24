package com.unfamiliardev.bbc.data.parser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u001c\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u000f\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/unfamiliardev/bbc/data/parser/M3UParser;", "", "()V", "ATTR_REGEX", "Lkotlin/text/Regex;", "EXTINF_REGEX", "parse", "", "Lcom/unfamiliardev/bbc/data/model/Channel;", "content", "", "playlistId", "", "parseAttrs", "", "attrString", "app_debug"})
public final class M3UParser {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex EXTINF_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex ATTR_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.unfamiliardev.bbc.data.parser.M3UParser INSTANCE = null;
    
    private M3UParser() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unfamiliardev.bbc.data.model.Channel> parse(@org.jetbrains.annotations.NotNull()
    java.lang.String content, long playlistId) {
        return null;
    }
    
    private final java.util.Map<java.lang.String, java.lang.String> parseAttrs(java.lang.String attrString) {
        return null;
    }
}