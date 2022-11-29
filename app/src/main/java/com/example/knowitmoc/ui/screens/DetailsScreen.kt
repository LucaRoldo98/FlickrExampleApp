package com.example.knowitmoc.ui.screens

import android.text.format.DateFormat
import android.widget.TextView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.knowitmoc.FlickrPhotosApplication
import com.example.knowitmoc.R
import com.example.knowitmoc.ui.AnimatedGrayRectangle
import com.example.knowitmoc.ui.theme.FlickrBlue
import com.example.knowitmoc.ui.theme.FlickrMagenta
import com.example.knowitmoc.ui.theme.KnowItMOCTheme
import java.util.*

val exampleDetailedPhoto = FlickrPhotoInfo("11", "Pollo Arrosto", "Luca Roldo", "https://i.kym-cdn.com/entries/icons/original/000/026/489/crying.jpg", "Sad cat meme", "This cat is so sad that it started crying", "2022-11-26 18:21:22", "1669539755", 83, 3, "sss")
val emptyDetailedPhoto = FlickrPhotoInfo(null, null, null, null, null, null, null, null, null, null, null)

// Function for converting timestamp to String representing a date
fun getDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = timestamp * 1000L
    return DateFormat.format("yyyy-MM-dd hh:mm:ss", calendar).toString()
}

// Photos descriptions are in html format for hrefs
@Composable
fun HtmlText(html: String, modifier : Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)})
}

@Composable
fun DetailsScreen(
    viewModel: FlickrViewModel,
    detailScreenUiState: DetailScreenUiState,
    modifier: Modifier = Modifier
) {
    when (detailScreenUiState) {
        is DetailScreenUiState.Loading -> DetailedScreenChild(viewModel, emptyDetailedPhoto, modifier)
        is DetailScreenUiState.Success -> DetailedScreenChild(viewModel, detailScreenUiState.detailedPhoto, modifier)
        is DetailScreenUiState.Error -> ErrorDetailScreen(modifier)
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun DetailedScreenChild(viewModel: FlickrViewModel, photoDetailed: FlickrPhotoInfo, modifier: Modifier = Modifier) {

    val isLoading = (viewModel.detailScreenUiState == DetailScreenUiState.Loading) or (viewModel.detailScreenUiState == DetailScreenUiState.Error)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var alpha by remember { mutableStateOf( (0.5 + Math.random() * 0.5).toFloat() ) }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

    Spacer(modifier = Modifier.size(16.dp))

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (isLoading) {
        Box(
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Gray)) }
        else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoDetailed.iconUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                placeholder = painterResource(id = R.drawable.gray_square),
                error = painterResource(id = R.drawable.gray_square),
                onSuccess = { alpha = 1f },
                onError = { alpha = 1f },
                alpha = alpha,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }

        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(start = 8.dp)) {
        if (isLoading) {
                AnimatedGrayRectangle(height = 16.dp, width = screenWidth.div(5))
                AnimatedGrayRectangle(height = 16.dp, width = screenWidth.div(4))
        }
        else {

                Text(text = photoDetailed.username!!,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(text = photoDetailed.realname!!,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }

    Spacer(Modifier.size(16.dp))

    if(isLoading) {
        AnimatedGrayRectangle(height = screenWidth, width = screenWidth)
    } else {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photoDetailed.photoUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        placeholder = painterResource(id = R.drawable.gray_square),
        error = painterResource(id = R.drawable.not_loaded),
        onSuccess = { alpha = 1f },
        onError = { alpha = 1f },
        alpha = alpha,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier
            .size(screenWidth)
    ) }

    Spacer(modifier = Modifier.size(16.dp))

   // Need another column to align everything to the left
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        if (isLoading) {
            AnimatedGrayRectangle(height = 40.dp, width = screenWidth.times(0.9f), modifier = Modifier.padding(horizontal = 4.dp))
        } else {
            Text(
                text = photoDetailed.title!!,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                fontWeight = FontWeight.Black,
                overflow = TextOverflow.Ellipsis,
                lineHeight = TextUnit(1f, TextUnitType.Em)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        if (isLoading) {
            AnimatedGrayRectangle(height = 80.dp, width = screenWidth.times(0.9f), modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.size(8.dp))
        } else {

            HtmlText(
                html = photoDetailed.description!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                )

            /*Text(
                text = photoDetailed.description!!,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                lineHeight = TextUnit(1f, TextUnitType.Em)
            ) */

            if (photoDetailed.description.isNotEmpty()) {
                Spacer(Modifier.size(16.dp))
            }
        }

        if (isLoading) {
            AnimatedGrayRectangle(height = 16.dp, width = screenWidth.times(0.2f), modifier = Modifier.padding(horizontal = 4.dp))
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(Icons.Filled.Visibility, "Eye",
                    tint = FlickrMagenta,
                    modifier = Modifier.size(24.dp).padding(end = 4.dp))

                Text(
                    text = "${photoDetailed.views!!} views",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = TextUnit(1f, TextUnitType.Em)
                )
            }
        }

        if (isLoading) {
            AnimatedGrayRectangle(height = 16.dp, width = screenWidth.times(0.23f), modifier = Modifier.padding(horizontal = 4.dp))
        } else {

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Comment,
                        "Eye",
                        tint = FlickrBlue,
                        modifier = Modifier.size(24.dp).padding(end = 4.dp)
                    )

                    Text(
                        text = "${photoDetailed.comments!!} comments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = TextUnit(1f, TextUnitType.Em)
                    )
                }

        }

        Spacer(Modifier.size(16.dp))

        if (isLoading) {
            AnimatedGrayRectangle(height = 16.dp, width = screenWidth.times(0.33f), modifier = Modifier.padding(horizontal = 4.dp))
        } else {
                Text(
                    text = "Uploaded: ${getDate( photoDetailed.dateuploaded!!.toLong())}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = TextUnit(1f, TextUnitType.Em)
                )
        }

        if (isLoading) {
            AnimatedGrayRectangle(height = 16.dp, width = screenWidth.times(0.3f), modifier = Modifier.padding(horizontal = 4.dp))
        } else {
                Text(
                    text = "Taken: ${photoDetailed.datetaken!!}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = TextUnit(1f, TextUnitType.Em)
                )
        }
    }

        Spacer(Modifier.size(16.dp))
    }
}

@Composable
fun ErrorDetailScreen(modifier : Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.size(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    AnimatedGrayRectangle(height = 16.dp, width = screenWidth.div(5), animate = false)
                    AnimatedGrayRectangle(height = 16.dp, width = screenWidth.div(4), animate = false)
                }
            }

            Spacer(Modifier.size(16.dp))

            AnimatedGrayRectangle(height = screenWidth, width = screenWidth, animate = false)

            Spacer(modifier = Modifier.size(16.dp))

            // Need another column to align everything to the left
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {

                AnimatedGrayRectangle(
                    height = 40.dp,
                    width = screenWidth.times(0.9f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )


                Spacer(modifier = Modifier.size(8.dp))


                AnimatedGrayRectangle(
                    height = 80.dp,
                    width = screenWidth.times(0.9f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )

                Spacer(Modifier.size(8.dp))

                AnimatedGrayRectangle(
                    height = 16.dp,
                    width = screenWidth.times(0.2f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )

                AnimatedGrayRectangle(
                    height = 16.dp,
                    width = screenWidth.times(0.23f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )


                Spacer(Modifier.size(16.dp))


                AnimatedGrayRectangle(
                    height = 16.dp,
                    width = screenWidth.times(0.33f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )

                AnimatedGrayRectangle(
                    height = 16.dp,
                    width = screenWidth.times(0.3f),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    animate = false
                )

            }

            Spacer(Modifier.size(16.dp))
        }
        val position = remember { Animatable(0f) }
        val distance = with(LocalDensity.current) { 32.dp.toPx() }

        LaunchedEffect(key1 = position) {
            position.animateTo(
                targetValue = 1f,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        }

        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .graphicsLayer {
                        translationY = -position.value * distance
                    }
                    .shadow(16.dp, clip = true),
                color = FlickrMagenta,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Connection error",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailedScreenChild() {
    KnowItMOCTheme {
        DetailsScreen(viewModel = FlickrViewModel(FlickrPhotosApplication().container.flickrPhotosRepository), detailScreenUiState = DetailScreenUiState.Success(
            exampleDetailedPhoto))
    }

}