package com.tyomsky.empublite;

import retrofit.http.GET;

public interface BookUpdateInterface {

    @GET("/misc/empublite-update.json")
    BookUpdateInfo update();

}
