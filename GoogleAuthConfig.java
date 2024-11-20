import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleAuthConfig {
    private static final String CLIENT_ID = "51926553506-0uh010cv5kjmkd4nktb29pej4brogj92.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
        "https://www.googleapis.com/auth/userinfo.profile",
        "https://www.googleapis.com/auth/userinfo.email"
    );
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private GoogleAuthorizationCodeFlow flow;
    private LocalServerReceiver receiver;
    private GoogleIdTokenVerifier verifier;

    public GoogleAuthConfig() {
        initializeConfig();
    }

    private void initializeConfig() {
        try {
            // Load client secrets from config/client_secrets.json
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream("config/client_secrets.json")));

            // Build flow and trigger user authorization request
            flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY_PATH).toFile()))
                .setAccessType("offline")
                .build();

            // Initialize the token verifier
            verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

            // Initialize the local server receiver
            receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Google Auth configuration", e);
        }
    }

    public Credential getCredentials(String userId) throws IOException {
        return new AuthorizationCodeInstalledApp(flow, receiver)
            .authorize(userId);
    }

    public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify ID token", e);
        }
        return null;
    }

    public String getAuthorizationUrl(String userId) {
        return flow.newAuthorizationUrl()
            .setRedirectUri(REDIRECT_URI)
            .setState(userId)
            .build();
    }

    public GoogleAuthorizationCodeFlow getFlow() {
        return flow;
    }

    public LocalServerReceiver getReceiver() {
        return receiver;
    }

    public void shutdown() {
        try {
            if (receiver != null) {
                receiver.stop();
            }
        } catch (IOException e) {
            // Log the error but don't throw
            e.printStackTrace();
        }
    }

    // Helper method to extract user information from the ID token payload
    public static class UserInfo {
        private final String email;
        private final String name;
        private final String pictureUrl;

        public UserInfo(GoogleIdToken.Payload payload) {
            this.email = payload.getEmail();
            this.name = (String) payload.get("name");
            this.pictureUrl = (String) payload.get("picture");
        }

        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getPictureUrl() { return pictureUrl; }
    }

    public UserInfo getUserInfo(String idToken) {
        GoogleIdToken.Payload payload = verifyIdToken(idToken);
        return payload != null ? new UserInfo(payload) : null;
    }
}