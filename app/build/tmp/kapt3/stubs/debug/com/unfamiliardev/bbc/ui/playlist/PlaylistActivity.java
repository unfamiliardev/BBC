package com.unfamiliardev.bbc.ui.playlist;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\u001a\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0010H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u001a"}, d2 = {"Lcom/unfamiliardev/bbc/ui/playlist/PlaylistActivity;", "Landroidx/fragment/app/FragmentActivity;", "()V", "adapter", "Lcom/unfamiliardev/bbc/ui/playlist/PlaylistAdapter;", "binding", "Lcom/unfamiliardev/bbc/databinding/ActivityPlaylistBinding;", "konamiDetector", "Lcom/unfamiliardev/bbc/util/KonamiCodeDetector;", "viewModel", "Lcom/unfamiliardev/bbc/ui/playlist/PlaylistViewModel;", "getViewModel", "()Lcom/unfamiliardev/bbc/ui/playlist/PlaylistViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onKeyDown", "", "keyCode", "", "event", "Landroid/view/KeyEvent;", "submitPlaylist", "app_debug"})
public final class PlaylistActivity extends androidx.fragment.app.FragmentActivity {
    private com.unfamiliardev.bbc.databinding.ActivityPlaylistBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.unfamiliardev.bbc.ui.playlist.PlaylistAdapter adapter;
    @org.jetbrains.annotations.NotNull()
    private final com.unfamiliardev.bbc.util.KonamiCodeDetector konamiDetector = null;
    
    public PlaylistActivity() {
        super();
    }
    
    private final com.unfamiliardev.bbc.ui.playlist.PlaylistViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void submitPlaylist() {
    }
    
    @java.lang.Override()
    public boolean onKeyDown(int keyCode, @org.jetbrains.annotations.Nullable()
    android.view.KeyEvent event) {
        return false;
    }
}