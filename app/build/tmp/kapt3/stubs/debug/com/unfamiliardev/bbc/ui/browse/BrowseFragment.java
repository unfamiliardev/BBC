package com.unfamiliardev.bbc.ui.browse;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0002J\u0012\u0010\f\u001a\u00020\b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\u001a\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/unfamiliardev/bbc/ui/browse/BrowseFragment;", "Landroidx/leanback/app/BrowseSupportFragment;", "()V", "rowsAdapter", "Landroidx/leanback/widget/ArrayObjectAdapter;", "viewModel", "Lcom/unfamiliardev/bbc/ui/browse/BrowseViewModel;", "buildRows", "", "channels", "", "Lcom/unfamiliardev/bbc/data/model/Channel;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "view", "Landroid/view/View;", "Companion", "app_debug"})
public final class BrowseFragment extends androidx.leanback.app.BrowseSupportFragment {
    private com.unfamiliardev.bbc.ui.browse.BrowseViewModel viewModel;
    @org.jetbrains.annotations.NotNull()
    private final androidx.leanback.widget.ArrayObjectAdapter rowsAdapter = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_MANAGE = "action_manage";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_REFRESH = "action_refresh";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_CREDITS = "action_credits";
    @org.jetbrains.annotations.NotNull()
    public static final com.unfamiliardev.bbc.ui.browse.BrowseFragment.Companion Companion = null;
    
    public BrowseFragment() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void buildRows(java.util.List<com.unfamiliardev.bbc.data.model.Channel> channels) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/unfamiliardev/bbc/ui/browse/BrowseFragment$Companion;", "", "()V", "ACTION_CREDITS", "", "ACTION_MANAGE", "ACTION_REFRESH", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}