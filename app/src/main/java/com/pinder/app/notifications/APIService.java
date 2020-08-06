package com.pinder.app.notifications;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhsio7d4:APA91bHDcW5F8M_xqUZ88bdz2IvZH2s7l-RIMc-ExFWevkT-7PjWOn909ZNaxEJfWbfv6q91CjxLaH8tCT5Hf0OuxWUqyQ1Ob8uNklCU36IMW-ymzuYtY1tPowsX472eL-y98218DYvu"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Sender body);
}
