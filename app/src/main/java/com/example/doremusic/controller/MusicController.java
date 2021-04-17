package com.example.doremusic.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.MediaController;


public class MusicController extends MediaController {
    public MusicController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MusicController(Context context) {
        super(context);
    }
}
