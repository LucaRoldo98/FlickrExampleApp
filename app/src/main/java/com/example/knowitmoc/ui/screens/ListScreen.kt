package com.example.knowitmoc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.knowitmoc.FlickrPhotosApplication
import com.example.knowitmoc.R
import com.example.knowitmoc.ui.LoadingAnimation
import com.example.knowitmoc.ui.navigation.Screens
import com.example.knowitmoc.ui.theme.FlickrMagenta
import com.example.knowitmoc.ui.theme.KnowItMOCTheme

@Composable
fun ListScreen(
    viewModel: FlickrViewModel,
    flickrUiState: FlickrUiState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    when (flickrUiState) {
        is FlickrUiState.Loading -> PhotosListScreen(viewModel, emptyList(), navController, modifier)
        is FlickrUiState.Success -> PhotosListScreen(viewModel, flickrUiState.photos, navController, modifier)
        is FlickrUiState.Error -> ErrorScreen(viewModel)
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhotosListScreen(viewModel : FlickrViewModel, flickrPhotos: List<FlickrPhoto>, navController: NavHostController, modifier: Modifier = Modifier)  {

    val interactionSource = remember { MutableInteractionSource() }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {

        Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.flickr_logo),
                        contentDescription = "Flickr Logo",
                        modifier = Modifier
                            .height(60.dp)
                            .padding(vertical = 10.dp)
                            .rotate(if (viewModel.toggleGrid) 0f else 180f)
                            .clickable(interactionSource = interactionSource, indication = null) {
                                viewModel.toggleGrid = !viewModel.toggleGrid
                            }
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.textField,
                            onValueChange = { viewModel.textField = it },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 4.dp),
                            maxLines = 1,
                            placeholder = { Text("Search...") },
                            shape = RoundedCornerShape(4.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.LightGray.copy(
                                    alpha = 0.4f
                                ), textColor = Color.DarkGray
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch =
                            { if (viewModel.textField.isNotEmpty()) {
                              viewModel.getFlickrPhotos()
                            } })
                        )

                        IconButton(onClick = {
                            if (viewModel.textField.isNotEmpty()) {
                                viewModel.getFlickrPhotos() }
                        }) {

                            Box(
                                modifier = Modifier
                                    .size(600.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    , contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search button",
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }
                        }

                    }

                    Spacer(modifier = Modifier.size(20.dp))

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }
            }

        if (viewModel.flickrUiState is FlickrUiState.Loading) {
            LoadingAnimation(modifier = Modifier.fillMaxSize())
        }

        // Research doesn't produce results
        else if (flickrPhotos.isEmpty() and (viewModel.flickrUiState!=FlickrUiState.Error) and (viewModel.flickrUiState!=FlickrUiState.Loading)) {
            Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No photos found :(", color = Color.Gray, style = MaterialTheme.typography.displaySmall)
            }
        }

        else if (!viewModel.toggleGrid) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(flickrPhotos) { photo ->
                    ImageCard(flickrPhoto = photo, viewModel = viewModel, navController = navController, modifier = Modifier.animateItemPlacement(tween(durationMillis = 250)))
                }
            }
        }
        else {
            val imageSize = (LocalConfiguration.current.screenWidthDp.dp - 2.dp.times(4)).div(3)
            LazyVerticalGrid(columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(vertical = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
                items(items = flickrPhotos, key = {photo -> photo.id} ) { photo ->
                    ImageAlone(photo, imageSize, viewModel, navController,
                        Modifier
                            .padding(1.dp)
                            .animateItemPlacement(tween(durationMillis = 250)))
                }
            }
        }
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(viewModel : FlickrViewModel, modifier: Modifier = Modifier) {

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    painter = painterResource(id = R.drawable.flickr_logo),
                    contentDescription = "Flickr Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .padding(vertical = 10.dp)
                        .rotate(if (viewModel.toggleGrid) 0f else 180f)
                        .clickable(interactionSource = interactionSource, indication = null) {
                            viewModel.toggleGrid = !viewModel.toggleGrid
                        }
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.textField,
                        onValueChange = { viewModel.textField = it },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(horizontal = 4.dp),
                        maxLines = 1,
                        placeholder = { Text("Search...") },
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.LightGray.copy(
                                alpha = 0.4f
                            ), textColor = Color.DarkGray
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch =
                        {
                            if (viewModel.textField.isNotEmpty()) {
                                viewModel.getFlickrPhotos()
                            }
                        })
                    )

                    IconButton(onClick = {
                        if (viewModel.textField.isNotEmpty()) {
                            viewModel.getFlickrPhotos()
                        }
                    }) {

                        Box(
                            modifier = Modifier
                                .size(600.dp)
                                .clip(CircleShape)
                                .background(Color.Black), contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search button",
                                tint = Color.White,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.size(20.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = Color.Black
                )
            }
        }


        val position = remember { Animatable(0f) }
        val distance = with(LocalDensity.current) { 32.dp.toPx() }

        LaunchedEffect(key1 = position) {
            position.animateTo(
                targetValue = 1f,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        }

        Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .graphicsLayer {
                        translationY = - position.value * distance
                    }
                    .shadow(16.dp, clip = true)
                ,
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

@Composable
fun ImageAlone(flickrPhoto: FlickrPhoto, imageSize : Dp, viewModel: FlickrViewModel , navHostController: NavHostController, modifier : Modifier = Modifier) {

    var alpha by remember { mutableStateOf( (0.5 + Math.random() * 0.5).toFloat() ) }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(flickrPhoto.imageUrl)
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
            .size(imageSize)
            .clip(RoundedCornerShape(2.dp))
            .clickable {
                navHostController.navigate(Screens.Details.route)
                viewModel.detailedPhotoID = flickrPhoto.id
                viewModel.getPhotoDetails()
            }

    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun ImageCard(flickrPhoto: FlickrPhoto, navController: NavHostController, viewModel : FlickrViewModel, modifier : Modifier = Modifier) {

    val spacersSize = 16.dp
    val photoHeight = 160.dp
    var alpha by remember { mutableStateOf( (0.5 + Math.random() * 0.5).toFloat() ) }

    Card(onClick = {
            navController.navigate(Screens.Details.route)
            viewModel.detailedPhotoID = flickrPhoto.id
            viewModel.getPhotoDetails()
                   },
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Spacer(Modifier.size(spacersSize))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(flickrPhoto.imageUrl)
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
                modifier = Modifier
                    .size(photoHeight)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(photoHeight)
                    .padding(4.dp)
            ) {

                Text(
                    text = flickrPhoto.imageName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Black,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = TextUnit(1f, TextUnitType.Em),
                    maxLines = 4
                )


                Text(
                    text = flickrPhoto.ownername,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

            }
        }

        Spacer(Modifier.size(spacersSize))

    }
}



@Preview(showBackground = true)
@Composable
fun ImageCardPreview() {
    KnowItMOCTheme {
        ImageCard(FlickrPhoto("1", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), viewModel = FlickrViewModel(FlickrPhotosApplication().container.flickrPhotosRepository), navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    KnowItMOCTheme {
        ListScreen(viewModel = FlickrViewModel(FlickrPhotosApplication().container.flickrPhotosRepository), flickrUiState = FlickrUiState.Success(exampleList), navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosListScreenPreview() {
    KnowItMOCTheme {
        PhotosListScreen(viewModel = FlickrViewModel(FlickrPhotosApplication().container.flickrPhotosRepository), flickrPhotos = exampleList, navController = rememberNavController())
    }
}
