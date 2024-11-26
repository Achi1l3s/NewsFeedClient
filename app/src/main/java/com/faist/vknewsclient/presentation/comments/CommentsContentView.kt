package com.faist.vknewsclient.presentation.comments

import android.os.Build
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.faist.vknewsclient.data.model.AttachmentDto
import com.faist.vknewsclient.data.model.MediaType
import com.faist.vknewsclient.data.model.MediaType.AUDIO
import com.faist.vknewsclient.data.model.MediaType.GIF
import com.faist.vknewsclient.data.model.MediaType.IMAGE
import com.faist.vknewsclient.data.model.MediaType.TEXT
import com.faist.vknewsclient.data.model.MediaType.UNKNOWN
import com.faist.vknewsclient.data.model.MediaType.VIDEO
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView



/**** Need to remake ****/


@Composable
private fun CommentWithMedia(content: String, mediaType: MediaType) {
    when (mediaType) {
        IMAGE -> {
            StaticImage(content)
            Log.d("MediaCheck", "IMAGE: $content")
        }

        GIF -> {
            GifImage(content)
            Log.d("MediaCheck", "GIF: $content")
        }

        VIDEO -> {
            VideoPlayer(content)
            Log.d("MediaCheck", "VIDEO: $content")
        }

        TEXT -> {
            TextComment(content)
            Log.d("MediaCheck", "IMAGE: $content")
        }

        AUDIO -> VkAudioPlayer(content)
        UNKNOWN -> Text("Неподдерживаемый тип", color = Color.Gray)
    }
}

fun detectMediaType(attachment: AttachmentDto): MediaType {
    val type: MediaType = when (attachment.type) {
        "photo" -> IMAGE
        "video" -> VIDEO
        "audio" -> AUDIO
        "doc" -> {
            when (attachment.doc?.type) {
                3 -> GIF
                4 -> IMAGE
                5 -> AUDIO
                6 -> VIDEO
                else -> UNKNOWN
            }
        }

        else -> UNKNOWN
    }
    return type
}

@Composable
fun TextComment(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Start
    )
}

@Composable
fun StaticImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Static Image",
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(2.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Укажите соотношение сторон видео
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun GifImage(url: String) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory()) // Для Android P и выше
            } else {
                add(GifDecoder.Factory()) // Для более ранних версий
            }
        }
        .build()

    Image(
        painter = rememberImagePainter(
            data = url,
            imageLoader = imageLoader
        ),
        contentDescription = "GIF Image",
        modifier = Modifier.size(100.dp) // Укажите размер изображения по вашему желанию
    )
}

@Composable
fun VkAudioPlayer(audioUrl: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(audioUrl) // Загружаем аудио URL "ВКонтакте"
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
