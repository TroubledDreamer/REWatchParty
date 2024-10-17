import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface YouTubeAPI {

    @GET("search")
    fun searchVideos(
        @Query("part") part: String ="snippet",
        @Query("type") type: String = "video",
        @Query("q") query: String)
    ): YouTubeSearchResponse


}