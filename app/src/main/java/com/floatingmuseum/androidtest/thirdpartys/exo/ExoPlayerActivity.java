package com.floatingmuseum.androidtest.thirdpartys.exo;

import android.database.Cursor;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/7/4.
 */

public class ExoPlayerActivity extends BaseActivity implements ExoPlayer.EventListener {

    @BindView(R.id.sepv)
    SimpleExoPlayerView sepv;
    @BindView(R.id.bt_exo_play)
    Button btExoPlay;
    @BindView(R.id.bt_exo_stop)
    Button btExoStop;
    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private List<MusicInfo> musicList = new ArrayList<>();
    private ConcatenatingMediaSource mediaSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo);
        ButterKnife.bind(this);
        initView();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        extractorsFactory = new DefaultExtractorsFactory();

        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);


/*        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "mediaPlayerSample"),
                (TransferListener<? super DataSource>) bandwidthMeter);*/

        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), defaultBandwidthMeter);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        sepv.setPlayer(player);
        player.addListener(this);
        getMusicList();
    }

    private void initView() {
        btExoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        btExoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlayWhenReady(false);
            }
        });
    }

    private void playMusic() {
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private void getMusicList() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/netease/cloudmusic/Music";
        Logger.d("Music信息...path:" + path);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 是否为音乐
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }
                // 音乐uri
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (!uri.contains(path)) {
                    continue;
                }
                // ID
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
                // 标题
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                // 艺术家
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                // 专辑
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                // 持续时间
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                // 专辑封面id，根据该id可以获得专辑图片uri
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
//            val coverUri = getCoverUri(albumId);
                // 音乐文件名
//            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                // 音乐文件大小
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
                // 发行时间
                int year = cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR)));
                MusicInfo music = new MusicInfo(id, title, artist, album, uri, duration, albumId, fileSize, year);

                musicList.add(music);
            }

            cursor.close();
            List<MediaSource> mediaSources = new ArrayList<>();
            for (MusicInfo info : musicList) {
                Logger.d("Music信息:" + info.toString());
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(info.getUri()), dataSourceFactory, extractorsFactory, null, null);
                mediaSources.add(mediaSource);
            }
//            new LoopingMediaSource();
            MediaSource[] sources = new MediaSource[mediaSources.size()];
            mediaSources.toArray(sources);
            //DynamicConcatenatingMediaSource
            Logger.d("ExoPlayer...播放列表:" + sources.length);
            mediaSource = new ConcatenatingMediaSource(sources);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Logger.d("ExoPlayer...onTimelineChanged");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Logger.d("ExoPlayer...onTracksChanged");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Logger.d("ExoPlayer...onLoadingChanged...isLoading:" + isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (ExoPlayer.STATE_BUFFERING == playbackState) {
            Logger.d("ExoPlayer...onPlayerStateChanged...playWhenReady:" + playWhenReady + "...playbackState:Buffering");
        } else if (ExoPlayer.STATE_IDLE == playbackState) {
            Logger.d("ExoPlayer...onPlayerStateChanged...playWhenReady:" + playWhenReady + "...playbackState:Idle");
        } else if (ExoPlayer.STATE_ENDED == playbackState) {
            Logger.d("ExoPlayer...onPlayerStateChanged...playWhenReady:" + playWhenReady + "...playbackState:Ended");
        } else if (ExoPlayer.STATE_READY == playbackState) {
            Logger.d("ExoPlayer...onPlayerStateChanged...playWhenReady:" + playWhenReady + "...playbackState:Ready");
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Logger.d("ExoPlayer...onPlayerError...error:" + error.toString());
        error.printStackTrace();
    }

    @Override
    public void onPositionDiscontinuity() {
        Logger.d("ExoPlayer...onPositionDiscontinuity");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Logger.d("ExoPlayer...onPlaybackParametersChanged");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.removeListener(this);
        player.release();
    }
}
