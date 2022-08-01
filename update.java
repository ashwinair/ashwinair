///usr/bin/env jbang "$0" "$@" ; exit $?

//REPOS mavencentral,jitpack
//DEPS io.quarkus:quarkus-bom:${quarkus.version:2.2.0.CR1}@pom
//DEPS io.quarkus:quarkus-qute
//DEPS https://github.com/w3stling/rssreader/tree/v2.5.0
//DEPS io.quarkus:quarkus-rest-client-reactive
//DEPS io.quarkus:quarkus-rest-client-reactive-jackson
//DEPS com.googlecode.json-simple:json-simple:1.1.1

//JAVA 16+

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import io.quarkus.qute.Engine;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import netscape.javascript.JSObject;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@QuarkusMain
public class update implements QuarkusApplication {

    @Inject
    Engine qute;

     int totalNo;


    public int run(String... args) throws Exception {
        List<String> contributions = new ArrayList<>();
        String bio = """     
                         I am Ashwin Nair, a Computer Science student and an Open source enthusiast from INDIA.
                         I'm currently trying to improve my coding/programming skills and learning how to be a good software engineer by contributing to the various open-source projects and by creating side projects.
                                          
                         * 🔭  I’m currently working on something cool 😉
                         * 🌱  I’m currently learning  Go, Docker
                         * 📫  How to reach me: ashwinnair0007@gmail.com
                         * ⚡  Fun fact: I ❤️ 🐶s and FPP Gaming.
                         * are you participating in any hackathons and want someone on your team?? please ping me, 
                         I would love to join :)
                """;
        try {
            URL url = new URL("https://api.github.com/users/ashwinair/events/public");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                String inline = "";
                Scanner scan = new Scanner(url.openStream());
                while (scan.hasNext()) {
                    inline += scan.nextLine();
                    //parse json here
                }
                scan.close();
                JSONParser parser = new JSONParser();
                JSONArray arr = (JSONArray) parser.parse(inline);


                //we know the list only contain last 30 entries
                for (int i = 0; i < 30; i++) {
                    JSONObject JsonObj = (JSONObject) arr.get(i);
                    //System.out.println(obj.get("type"));
                    if(JsonObj.get("type").equals("PullRequestEvent"))  {
                        System.out.println(i);
                        totalNo++;
                        JSONObject payLoad  = (JSONObject) JsonObj.get("payload");
                        JSONObject  pull_request = (JSONObject ) payLoad.get("pull_request");

                        String html_url = (String) pull_request.get("html_url");
                        System.out.println(html_url);
                        contributions.add(html_url);
                    }
                    if(JsonObj.get("type").equals("PushEvent")){
                        totalNo++;
                    }
                }

            }else{
                throw new RuntimeException("HttpResponseCode: " + conn.getResponseCode());
            }
            }catch (Exception e) {
            e.printStackTrace();
        }

        Files.writeString(Path.of("readme.adoc"), qute.parse(Files.readString(Path.of("template.adoc.qute")))
                .data("bio", bio)
                .data("contributions", contributions)
                .data("totalNoOfContributions",totalNo)
                .render());

        return 0;

    }

}