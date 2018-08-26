package io.sweers.blackmirror.neighbor;

import com.serjltt.moshi.adapters.Wrapped;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NeighborApi {
  @Multipart
  @Headers("Authorization: Client-ID f6b6c9586f097ac")
  @POST("image")
  @Wrapped(path = {"data", "link"})
  Single<String> postImage(@Part MultipartBody.Part file);

  @GET("superprivate")
  Single<Object> getPrivateInfo();

  @POST("createuser")
  Single<Object> createUser(String userId);
}
