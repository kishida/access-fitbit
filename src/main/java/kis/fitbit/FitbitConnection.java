package kis.fitbit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import kis.fitbit.DailySleep.RangeSleep;

/**
 * @author naoki
 */
public class FitbitConnection {
    private static final String AUTHORIZATION_URL = "https://www.fitbit.com/oauth2/authorize";
    private static final String TOKEN_SERVER_URL = "https://api.fitbit.com/oauth2/token";
    
    private final HttpTransport transport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();

    private final String apiKey;
    private final String apiSecret;

    private Credential cred;
    
    public FitbitConnection(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    
    public void authorize(String username) {
        try {
            var tokenServerUrl = new GenericUrl(TOKEN_SERVER_URL);
            var flow = new AuthorizationCodeFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    transport, jsonFactory, tokenServerUrl,
                    new BasicAuthentication(apiKey, apiSecret),
                    apiKey, AUTHORIZATION_URL)
                    .setScopes(List.of("activity", "heartrate", "sleep", "settings"))
                    .setDataStoreFactory(new FileDataStoreFactory(new File("./fitbit_data")))
                    .build();
            var receiver = new LocalServerReceiver.Builder()
                    .setHost("localhost")
                    .setPort(8384).build();
            cred = new AuthorizationCodeInstalledApp(flow, receiver).authorize(username);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private <T> T retrieveData(String url, Class<T> clazz) {
        return retrieveDataImpl(url, (mapper, content) -> mapper.readValue(content, clazz));
    }
    private <T> T retrieveData(String url, TypeReference<T> ref) {
        return retrieveDataImpl(url, (mapper, content) -> mapper.readValue(content, ref));
    }
    
    private interface F<T> {
        T apply(ObjectMapper mapper, InputStream content) throws IOException;
    }
    private <T> T retrieveDataImpl(String url, F<T> reader) {
        Objects.requireNonNull(cred);
        var reqFactory = transport.createRequestFactory(cred);
        try {
            HttpRequest request = reqFactory.buildGetRequest(
                    new GenericUrl("https://api.fitbit.com/1.2/" + url));
            try(var content = request.execute().getContent()) {
                var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                return reader.apply(mapper, content);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    // 日をまたがる睡眠データはあとの日付で取得される
    public DailySleep retrieveSleeps(String userName, LocalDate date) {
        return retrieveData("user/" + userName + "/sleep/date/" + date + ".json",
                DailySleep.class);
    }    

    public RangeSleep retrieveSleeps(String userName, LocalDate start, LocalDate end) {
        return retrieveData(
                String.format("user/%s/sleep/date/%s/%s.json", userName, start, end),
                RangeSleep.class);
    }
    
    public ActivitySteps retrieveSteps(String userName, LocalDate date) {
        return retrieveData(
                String.format("user/%s/activities/steps/date/%s/1d.json", userName, date),
                ActivitySteps.class);
    }
    public ActivitySteps.ActivityTrackerSteps retrieveTrackerSteps(String userName, LocalDate date) {
        return retrieveData(
                String.format("user/%s/activities/tracker/steps/date/%s/1d.json", userName, date),
                ActivitySteps.ActivityTrackerSteps.class);
    }
    
    public HeartBeats retrieveHeartBeats(String userName, LocalDate date) throws IOException {
        return retrieveData(
                String.format("user/%s/activities/heart/date/%s/1d.json", userName, date),
                HeartBeats.class);
    }
    
    public List<Device> retrieveDevices(String userName) throws IOException {
        return retrieveData(
                String.format("user/%s/devices.json", userName),
                new TypeReference<List<Device>>() {});
    }
    public String readData(String userName) throws IOException {
        Objects.requireNonNull(cred);
        var reqFactory = transport.createRequestFactory(cred);
        HttpRequest request = reqFactory.buildGetRequest(
                new GenericUrl("https://api.fitbit.com/1.2/user/" + userName + "/activities/steps/date/2018-06-01/2018-06-30.json"));
        return request.execute().parseAsString();
        
    }
}
