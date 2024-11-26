package com.faist.vknewsclient.presentation.comments

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.faist.vknewsclient.R
import com.faist.vknewsclient.data.model.MediaType
import com.faist.vknewsclient.data.model.MediaType.TEXT
import com.faist.vknewsclient.domain.entity.*
import com.faist.vknewsclient.presentation.comments.CommentsScreenState.*
import com.faist.vknewsclient.presentation.getApplicationComponent

@Composable
fun CommentsScreen(
    feedPost: FeedPost,
    onBackPressed: () -> Unit
) {
    val component = getApplicationComponent()
            .getCommentsScreenComponentFactory()
            .create(feedPost)

    val viewModel: CommentsViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState(Initial)

    CommentsScreenContent(
        screenState = screenState,
        onBackPressed = onBackPressed
    )
}

@Composable
fun CommentsScreenContent(
    screenState: State<CommentsScreenState>,
    onBackPressed: () -> Unit
) {
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
                            "CommentsScreen items { CommentItem() } BLOCK, comment: ${comment.commentText}")
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
                        }
                    }
                }
            }
        }

        Initial -> {}
        Loading -> {
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
/*            }
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
        */
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