package com.faist.vknewsclient.presentation.comments

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.faist.vknewsclient.R
import com.faist.vknewsclient.data.model.AttachmentDto
import com.faist.vknewsclient.data.model.MediaType
import com.faist.vknewsclient.data.model.MediaType.AUDIO
import com.faist.vknewsclient.data.model.MediaType.GIF
import com.faist.vknewsclient.data.model.MediaType.IMAGE
import com.faist.vknewsclient.data.model.MediaType.TEXT
import com.faist.vknewsclient.data.model.MediaType.UNKNOWN
import com.faist.vknewsclient.data.model.MediaType.VIDEO
import com.faist.vknewsclient.domain.FeedPost
import com.faist.vknewsclient.domain.PostComment
import com.faist.vknewsclient.presentation.comments.CommentsScreenState.Comments
import com.faist.vknewsclient.presentation.comments.CommentsScreenState.Initial
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun CommentsScreen(
    feedPost: FeedPost,
    onBackPressed: () -> Unit
) {
    val viewModel: CommentsViewModel = viewModel(
        factory = CommentsViewModelFactory(
            feedPost,
            application = LocalContext.current.applicationContext as Application
        )
    )
    val screenState = viewModel.screenState.observeAsState(Initial)


    when (val currentState = screenState.value) {
        is Comments -> {
            val titleText = currentState.feedPost
                .contentText
                .substringBefore(".")
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                text = "Комментарии к >>>${titleText.uppercase()}<<<",
                                maxLines = 2,
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { onBackPressed() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 64.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = currentState.comments,
                        key = { it.id - it.hashCode() }
                    ) { comment ->
                        Log.d(
                            "MediaCheck",
                            "CommentsScreen items { CommentItem() } BLOCK")
                        CommentItem(comment = comment)
                    }
                    item {
                        if (currentState.nextCommentsIsLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Blue)
                            }
                        } else {
                            LaunchedEffect(Unit) {
                                Log.d(
                                    "MediaCheck",
                                    "LaunchedEffect, CommentsScreen loadNextComments BLOCK"
                                )
                                viewModel.loadNextComments()
                            }
                        }
                    }
                }
            }
        }

        Initial -> {}
        CommentsScreenState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: PostComment
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
    ) {
        var contentUrl: String? by rememberSaveable { mutableStateOf(null) }
        var mediaType: MediaType by rememberSaveable { mutableStateOf(TEXT) }

        AsyncImage(
            model = comment.authorAvatarUrl,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                modifier = Modifier,
                text = comment.authorName,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
//            if (comment.commentText.isNotBlank()) {
            Text(
                text = comment.commentText,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
//            }
//            SideEffect {
//                comment.attachments?.forEach { attachment ->
//                    Log.d("MediaCheck", "comment.attachments?.forEach BLOCK")
//                    mediaType = detectMediaType(attachment)
//                    contentUrl = when (mediaType) {
//                        IMAGE -> attachment.photo?.sizes?.lastOrNull()?.url
//                        GIF -> attachment.doc?.url
//                        VIDEO -> attachment.video?.player
//                        TEXT -> comment.commentText
//                        AUDIO -> attachment.audio?.url
//                        UNKNOWN -> "UNKNOWN"
//                    }
//                }
//            }
//            contentUrl?.let {
//                CommentWithMedia(it, mediaType)
//            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.publicationDate,
                color = MaterialTheme.colors.onSecondary,
                fontSize = 9.sp
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = null,
                )
            }
        }
    }
}

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
































