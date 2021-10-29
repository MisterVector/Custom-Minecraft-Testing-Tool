package org.codespeak.cmtt.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import javax.net.ssl.HttpsURLConnection;
import org.codespeak.cmtt.Configuration;
import org.json.JSONObject;

/**
 * A class containing the response of checking the program's version
 *
 * @author Vector
 */
public class CheckVersionResponse {
   
    private final String requestVersion;
    private final Timestamp requestReleaseTime;
    private final String version;
    private final Timestamp releaseTime;

    private CheckVersionResponse(String requestVersion, Timestamp requestReleaseTime, String version, Timestamp releaseTime) {
        this.requestVersion = requestVersion;
        this.requestReleaseTime = requestReleaseTime;
        this.version = version;
        this.releaseTime = releaseTime;
    }
    
    /**
     * Gets the requested version
     * @return requested version
     */
    public String getRequestVersion() {
        return requestVersion;
    }
    
    /**
     * Gets the requested release time
     * @return requested release time
     */
    public Timestamp getRequestReleaseTime() {
        return requestReleaseTime;
    }
    
    /**
     * Gets the version
     * @return version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the release time
     * @return release time
     */
    public Timestamp getReleaseTime() {
        return releaseTime;
    }
    
    /**
     * Performs the check version query and returns a CheckVersionResponse object
     * @return CheckVersionResponse object from check version query
     */
    public static CheckVersionResponse checkVersion() throws ProgramException {
        URL url = null;
        HttpsURLConnection connection = null;
        
        try {
            url = new URL(Configuration.DISTRIBUTION_URL + "?query=check_program_version&slug=" + Configuration.PROGRAM_SLUG + "&current_version=" + Configuration.PROGRAM_VERSION);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "CustomMinecraftTestingTool/" + Configuration.PROGRAM_VERSION);
        } catch (IOException ex) {
            throw ProgramException.fromException(ex);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            String jsonString = sb.toString();
            JSONObject obj = new JSONObject(jsonString);
            int status = obj.getInt("status");
            
            if (status == 1) {
                JSONObject jsonContents = obj.getJSONObject("contents");
                String requestVersion = null;
                Timestamp requestReleaseTime = null;
                String version = null;
                Timestamp releaseTime = null;
                
                if (jsonContents.has("request_version")) {
                    requestVersion = jsonContents.getString("request_version");
                }
                
                if (jsonContents.has("request_release_time")) {
                    requestReleaseTime = Timestamp.valueOf(jsonContents.getString("request_release_time"));
                }
                
                if (jsonContents.has("version")) {
                    version = jsonContents.getString("version");
                }
                
                if (jsonContents.has("release_time")) {
                    releaseTime = Timestamp.valueOf(jsonContents.getString("release_time"));
                }
                
                return new CheckVersionResponse(requestVersion, requestReleaseTime, version, releaseTime);
            } else {
                int errorCode = obj.getInt("error_code");
                String errorMessage = obj.getString("error_message");

                throw new ProgramException(ErrorType.fromId(errorCode), new Exception(errorMessage));
            }
        } catch (IOException ex) {
            throw ProgramException.fromException(ex);
        }
    }
    
}
