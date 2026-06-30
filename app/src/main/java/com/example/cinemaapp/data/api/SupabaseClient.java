package com.example.cinemaapp.data.api;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class SupabaseClient {
    private static Retrofit retrofit = null;

    public static void reset() { retrofit = null; }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Cấu hình OkHttp để tự động nhét API Key vào mọi yêu cầu gửi lên Supabase
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder()
                            .addHeader("apikey", SupabaseConfig.API_KEY)
                            .addHeader("Authorization", "Bearer " + SupabaseConfig.API_KEY)
                            .addHeader("Content-Type", "application/json");
                            
                    if (chain.request().header("Prefer") == null) {
                        builder.addHeader("Prefer", "return=minimal");
                    }
                    
                    return chain.proceed(builder.build());
                }
            }).build();

            // Khởi tạo Retrofit kết nối tới đường dẫn gốc
            retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        }
        return retrofit;
    }
}