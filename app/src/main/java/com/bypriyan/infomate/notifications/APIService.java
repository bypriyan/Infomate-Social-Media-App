package com.bypriyan.infomate.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {


    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAWrR2uJQ:APA91bEsisf2-ksjcUyIFXpNvwQfuFThUv34V-f2k1yPsQ2HWkUqb9mvtvvMozsrw0n-WLVtx4CN59NGpRMLwgG0ZQK2J3i_vXZ_QMNxeqeccqvgI2Rf5jmy7ULYsk9OFmXSbCyq3epf"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);


}
