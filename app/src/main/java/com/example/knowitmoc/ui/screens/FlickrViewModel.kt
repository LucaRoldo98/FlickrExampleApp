package com.example.knowitmoc.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.network.HttpException
import com.example.knowitmoc.FlickrPhotosApplication
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

private const val FLICKR_API_KEY : String = "ffe18fb8c652300f3fb3c9ae8b8d692f"
private const val FLICKR_SECRET : String = "cfae3651ba534a9b"
private const val PHOTOS_PER_PAGE: Int = 500


val exampleList = listOf(FlickrPhoto("1", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("2", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("3", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("4", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("5", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("6", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"), FlickrPhoto("7", "https://upload.wikimedia.org/wikipedia/commons/5/55/LinoBanfi.jpg", "Luca Roldo", "Lino Banfi", "Luca Roldo"))

interface FlickrUiState {
    data class Success(val photos: List<FlickrPhoto>) : FlickrUiState
    object Error : FlickrUiState
    object Loading : FlickrUiState
}

interface DetailScreenUiState {
    data class Success(val detailedPhoto: FlickrPhotoInfo) : DetailScreenUiState
    object Error : DetailScreenUiState
    object Loading : DetailScreenUiState
}

class FlickrViewModel(private val flickrPhotosRepository: FlickrPhotosRepository) : ViewModel() {

    var flickrUiState: FlickrUiState by mutableStateOf(FlickrUiState.Success(emptyList()))
    var textField: String by mutableStateOf("")
    var toggleGrid: Boolean by mutableStateOf(false)

    // Another separate UI state for the detailed screen
    var detailScreenUiState: DetailScreenUiState by mutableStateOf(DetailScreenUiState.Loading)

    // save in the view model the data from the displayed detailed photo
    var detailedPhotoID : String by mutableStateOf( "" )

    // On start up show the most recent photos
    init {
        viewModelScope.launch {
            flickrUiState = FlickrUiState.Loading
            flickrUiState = try {
                val searchResponse = flickrPhotosRepository.getRecentPhotos()
                val photoList = searchResponse.photos.photo.map { photo ->
                    FlickrPhoto(id = photo.id,
                        imageUrl = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg",
                        imageName = photo.title,
                        ownerID = photo.owner,
                        ownername = photo.ownername)
                }
                FlickrUiState.Success(photoList)
            } catch (e: IOException) {
                FlickrUiState.Error
            } catch (e: HttpException) {
                FlickrUiState.Error
            }
        }
    }

    fun getFlickrPhotos() {
        viewModelScope.launch {
            flickrUiState = FlickrUiState.Loading
            flickrUiState = try {

                val searchResponse = flickrPhotosRepository.getFlickrPhotos(textField)
                val photoList = searchResponse.photos.photo.map { photo ->
                    //val owner = flickrPhotosRepository.getUsername(photo.owner).person.username._content
                    FlickrPhoto(id = photo.id,
                        imageUrl = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg",
                        imageName = photo.title,
                        ownerID = photo.owner,
                        ownername = photo.ownername)
                }
                FlickrUiState.Success(photoList)

            } catch (e: IOException) {
                FlickrUiState.Error
            } catch (e: HttpException) {
                FlickrUiState.Error
            }
        }
    }

    fun getPhotoDetails() {
        viewModelScope.launch {
            // Can still use the Ui states
            detailScreenUiState = DetailScreenUiState.Loading
            detailScreenUiState = try {

                val searchResponse = flickrPhotosRepository.getPhotoInfo(detailedPhotoID)
                val photo = searchResponse.photo // For readibility of the next code
                val detailedPhotoInfo = FlickrPhotoInfo(
                    id = detailedPhotoID,
                    username = photo.owner.username,
                    realname = photo.owner.realname,
                    photoUrl = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg",
                    title = photo.title._content,
                    description = photo.description._content,
                    datetaken = photo.dates.taken,
                    dateuploaded = photo.dates.posted,
                    views = photo.views.toIntOrNull(),
                    comments = photo.comments._content.toIntOrNull(),
                    iconUrl = "https://farm${photo.owner.iconfarm}.staticflickr.com/${photo.owner.iconserver}/buddyicons/${photo.owner.nsid}.jpg"
                )
                DetailScreenUiState.Success(detailedPhotoInfo)
            } catch (e: IOException) {
                DetailScreenUiState.Error
            } catch (e: HttpException) {
                DetailScreenUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlickrPhotosApplication)
                val flickrPhotosRepository = application.container.flickrPhotosRepository
                FlickrViewModel(flickrPhotosRepository = flickrPhotosRepository)
            }
        }
    }
}

// To get the photos
data class PhotoSearchResponse(val photos: PhotosMetaData)

data class PhotosMetaData(val page: Int, val photo: List<PhotoResponse>)

data class PhotoResponse(val id: String, val owner: String, val secret : String, val server : String, val farm : Int, val title : String, val ownername : String)

data class FlickrPhoto(val id : String, val imageUrl : String, val ownerID : String, val imageName : String, val ownername: String)

data class ContentFormat(val _content: String)

//To get the photo details

data class PhotoInfoResponse(val photo : PhotoDetailed, val stat : String)

data class PhotoDetailed(val id : String, val secret : String, val server: String, val farm: Int, val dateuploaded : String, val isfavourite : Int, val license : String, val safety_level : String, val owner : UserInfo, val title : ContentFormat, val description: ContentFormat, val dates : DatesFormat, val views : String, val comments : ContentFormat)

data class UserInfo(val nsid : String, val username : String, val realname : String, val iconserver: String, val iconfarm : Int, val location : String)

data class DatesFormat(val posted : String, val taken : String)

// I'll create another class for the detailed view photo
data class FlickrPhotoInfo(val id : String? = null, val username: String? = null, val realname: String? = null, val photoUrl: String? = null, val title : String? = null, val description : String? = null, val datetaken : String? = null, val dateuploaded: String? = null, val views : Int? = null, val comments : Int? = null, val iconUrl : String?)

interface FlickrPhotosRepository {

    suspend fun getFlickrPhotos(text: String) : PhotoSearchResponse

    suspend fun getRecentPhotos() : PhotoSearchResponse

    suspend fun getPhotoInfo(photoID: String?) : PhotoInfoResponse
}

interface FlickrApiService {

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1&per_page=${PHOTOS_PER_PAGE}&api_key=$FLICKR_API_KEY&extras=owner_name")
    suspend fun getPhotos(@Query("text") text : String?) : PhotoSearchResponse

    @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&per_page=${PHOTOS_PER_PAGE}&api_key=$FLICKR_API_KEY&extras=owner_name")
    suspend fun getRecentPhotos() : PhotoSearchResponse

    @GET("?method=flickr.photos.getInfo&format=json&nojsoncallback=1&api_key=$FLICKR_API_KEY")
    suspend fun getPhotoInfo(@Query("photo_id") photoID : String?) : PhotoInfoResponse
}

class NetworkFlickrPhotosRepository(
    private val flickrApiService: FlickrApiService,
) : FlickrPhotosRepository {

    override suspend fun getFlickrPhotos(text: String): PhotoSearchResponse = flickrApiService.getPhotos(text)

    override suspend fun getRecentPhotos() : PhotoSearchResponse = flickrApiService.getRecentPhotos()

    override suspend fun getPhotoInfo(photoID: String?) : PhotoInfoResponse = flickrApiService.getPhotoInfo(photoID)
}


