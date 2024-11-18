package com.faist.vknewsclient.presentation.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.faist.vknewsclient.R
import com.faist.vknewsclient.domain.FeedPost
import com.faist.vknewsclient.domain.StatisticItem
import com.faist.vknewsclient.domain.StatisticType
import java.util.Locale

@Composable
fun PostCard(
    modifier: Modifier,
    feedPost: FeedPost,
    onCommentClickListener: (StatisticItem) -> Unit,
    onLikeClickListener: (StatisticItem) -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = modifier
                .padding(8.dp)
        ) {
            PostHeader(feedPost)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = feedPost.contentText)
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = feedPost.contentImgUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(8.dp))
            Statistics(
                statistics = feedPost.statistics,
                onCommentClickListener = onCommentClickListener,
                onLikeClickListener = onLikeClickListener,
                isLiked = feedPost.isLiked
            )
        }
    }
}

@Composable
private fun PostHeader(
    feedPost: FeedPost
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = feedPost.communityImageUrl,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = feedPost.communityName,
                color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = feedPost.publicationDate,
                color = MaterialTheme.colors.onSecondary
            )
        }
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colors.onSecondary
        )
    }
}

@Composable
private fun Statistics(
    statistics: List<StatisticItem>,

    onCommentClickListener: (StatisticItem) -> Unit,
    onLikeClickListener: (StatisticItem) -> Unit,
    isLiked: Boolean
) {
    Row {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            val viewsCount = statistics.getItemByType(StatisticType.VIEWS)
            IconWithText(
                iconResId = R.drawable.ic_views_count,
                text = formatStatisticCount(viewsCount.count),
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val sharesCount = statistics.getItemByType(StatisticType.SHARES)
            IconWithText(
                iconResId = R.drawable.ic_share,
                text = formatStatisticCount(sharesCount.count),
            )
            val commentsCount = statistics.getItemByType(StatisticType.COMMENTS)
            IconWithText(
                iconResId = R.drawable.ic_comment,
                text = formatStatisticCount(commentsCount.count),
                onClickListener = {
                    onCommentClickListener(commentsCount)
                }
            )
            val likesCount = statistics.getItemByType(StatisticType.LIKES)
            IconWithText(
                modifier = if (isLiked) Modifier.size(24.dp) else Modifier.size(20.dp),
                iconResId = if (isLiked) {
                    R.drawable.ic_like_set
                } else {
                    R.drawable.ic_like
                },
                text = formatStatisticCount(likesCount.count),
                onClickListener = {
                    onLikeClickListener(likesCount)
                },
                tint = if (isLiked) Color.Red else MaterialTheme.colors.onSecondary
            )
        }
    }
}

private fun formatStatisticCount(count: Int): String {
    return if (count in 100_000..999999) {
        String.format(Locale.getDefault(),"%sk", (count / 1000))
    } else if (count in 1_000..99_999) {
        String.format(Locale.getDefault(),"%.1fk", (count / 1000f))
    } else if (count >= 1_000_000) {
        String.format(Locale.getDefault(),"%skk", (count / 1_000_000))
    } else {
        count.toString()
    }
}

private fun List<StatisticItem>.getItemByType(statisticType: StatisticType): StatisticItem {
    return this.find {
        it.type == statisticType
    } ?: throw IllegalStateException("getItemByType fun statisticType error")
}

@Composable
private fun IconWithText(
    modifier: Modifier = Modifier,
    iconResId: Int,
    text: String,
    onClickListener: (() -> Unit)? = null,
    tint: Color = MaterialTheme.colors.onSecondary
) {
    val myModifier = if (onClickListener == null) {
        Modifier
    } else {
        Modifier
            .clickable {
                onClickListener()
            }
    }
    Row(
        modifier = myModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = modifier
                .size(20.dp),
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = tint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = MaterialTheme.colors.onSecondary
        )
    }
}