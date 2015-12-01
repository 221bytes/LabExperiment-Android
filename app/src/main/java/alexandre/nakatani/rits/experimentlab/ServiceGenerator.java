package alexandre.nakatani.rits.experimentlab;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Alexandre Catalano aka 221Bytes on 5/26/15.
 */
public class ServiceGenerator
{
    private static RestAdapter.Builder builder = new RestAdapter.Builder().setClient(new OkClient(new OkHttpClient()));

    // No need to instantiate this class.
    private ServiceGenerator()
    {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl)
    {
        builder.setRequestInterceptor(new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request)
            {
                request.addHeader("Accept", "application/json");

            }
        });

        builder.setEndpoint(baseUrl);

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }


//    public static <S> S createService(Class<S> serviceClass, String baseUrl, final AccessToken accessToken)
//    {
//
//        builder.setEndpoint(baseUrl);
//
////        if (accessToken != null)
////        {
////
////            builder.setRequestInterceptor(new RequestInterceptor()
////            {
////                @Override
////                public void intercept(RequestFacade request)
////                {
////                    request.addHeader("Accept", "application/json");
////                    request.addHeader("Authorization", accessToken.getToken_type() + " " + accessToken.getAccess_token());
////
////                }
////            });
////        }
//
//        RestAdapter adapter = builder.build();
//
//        return adapter.create(serviceClass);
//    }


}

