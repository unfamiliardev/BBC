package com.unfamiliardev.bbc.ui.player;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000  2\u00020\u0001:\u0001 B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0012\u0010\u0013\u001a\u00020\u00102\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\b\u0010\u0016\u001a\u00020\u0010H\u0014J\u001a\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u0010H\u0014J\u0018\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f\u00a8\u0006!"}, d2 = {"Lcom/unfamiliardev/bbc/ui/player/PlayerActivity;", "Landroidx/fragment/app/FragmentActivity;", "()V", "binding", "Lcom/unfamiliardev/bbc/databinding/ActivityPlayerBinding;", "konamiDetector", "Lcom/unfamiliardev/bbc/util/KonamiCodeDetector;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "viewModel", "Lcom/unfamiliardev/bbc/ui/player/PlayerViewModel;", "getViewModel", "()Lcom/unfamiliardev/bbc/ui/player/PlayerViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "initPlayer", "", "url", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onKeyDown", "", "keyCode", "", "event", "Landroid/view/KeyEvent;", "onPause", "saveLastPlayed", "name", "Companion", "app_debug"})
public final class PlayerActivity extends androidx.fragment.app.FragmentActivity {
    private com.unfamiliardev.bbc.databinding.ActivityPlayerBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.exoplayer.ExoPlayer player;
    @org.jetbrains.annotations.NotNull()
    private final com.unfamiliardev.bbc.util.KonamiCodeDetector konamiDetector = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_URL = "extra_url";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_NAME = "extra_name";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREFS_NAME = "bbc_player";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_LAST_URL = "last_url";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_LAST_NAME = "last_name";
    @org.jetbrains.annotations.NotNull()
    public static final com.unfamiliardev.bbc.ui.player.PlayerActivity.Companion Companion = null;
    
    public PlayerActivity() {
        super();
    }
    
    private final com.unfamiliardev.bbc.ui.player.PlayerViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initPlayer(java.lang.String url) {
    }
    
    private final void saveLastPlayed(java.lang.String url, java.lang.String name) {
    }
    
    @java.lang.Override()
    public boolean onKeyDown(int keyCode, @org.jetbrains.annotations.Nullable()
    android.view.KeyEvent event) {
        return false;
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/unfamiliardev/bbc/ui/player/PlayerActivity$Companion;", "", "()V", "EXTRA_NAME", "", "EXTRA_URL", "KEY_LAST_NAME", "KEY_LAST_URL", "PREFS_NAME", "getLastPlayed", "Lkotlin/Pair;", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final kotlin.Pair<java.lang.String, java.lang.String> getLastPlayed(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}