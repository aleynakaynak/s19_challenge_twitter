package com.example.twitter_challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Gerçek Spring context, Security, JPA ve test veritabanı birlikte çalışır.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void completeFlow_checksAuthenticationOwnershipRelationsAndCors() throws Exception {
        mvc.perform(post("/register").contentType("application/json")
                .content("{\"userName\":\"owner\",\"email\":\"owner@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/register").contentType("application/json")
                .content("{\"userName\":\"other\",\"email\":\"other@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/login").contentType("application/json")
                .content("{\"userName\":\"owner\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/login").contentType("application/json")
                .content("{\"userName\":\"owner\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user.userName").value("owner"));
        mvc.perform(post("/tweet").contentType("application/json").content("{\"content\":\"Hi\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/tweet").with(httpBasic("owner", "password123"))
                .contentType("application/json").content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/tweet").with(httpBasic("owner", "password123"))
                .contentType("application/json").content("{\"content\":\"İlk tweet\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(put("/tweet/1").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"content\":\"No\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/tweet/1").with(httpBasic("owner", "password123"))
                .contentType("application/json").content("{\"content\":\"Güncel tweet\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").value("Güncel tweet"));
        mvc.perform(post("/comment/").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1,\"content\":\"Yorum\"}"))
                .andExpect(status().isCreated());
        mvc.perform(put("/comment/1").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"content\":\"Güncel yorum\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/like").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.likeCount").value(1));
        mvc.perform(post("/like").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/retweet/").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/tweet/findByUserId").param("userId", "1")
                .header("Origin", "http://localhost:3200"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3200"))
                .andExpect(jsonPath("$[0].likeCount").value(1))
                .andExpect(jsonPath("$[0].commentCount").value(1))
                .andExpect(jsonPath("$[0].retweetCount").value(1));
        mvc.perform(options("/tweet").header("Origin", "http://localhost:3200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("authorization")));
        mvc.perform(options("/tweet").header("Origin", "http://untrusted.example")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/retweet/1").with(httpBasic("owner", "password123")))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/retweet/1").with(httpBasic("other", "password123")))
                .andExpect(status().isNoContent());
        mvc.perform(post("/dislike").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.likeCount").value(0));
        mvc.perform(delete("/comment/1").with(httpBasic("owner", "password123")))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/tweet/1").with(httpBasic("other", "password123")))
                .andExpect(status().isForbidden());
        // İlişkili kayıtlar varken tweet silme de doğrulanır.
        mvc.perform(post("/comment/").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1,\"content\":\"Tekrar\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/like").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isOk());
        mvc.perform(post("/retweet/").with(httpBasic("other", "password123"))
                .contentType("application/json").content("{\"tweetId\":1}"))
                .andExpect(status().isCreated());
        mvc.perform(delete("/tweet/1").with(httpBasic("owner", "password123")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/tweet/findById").param("id", "1"))
                .andExpect(status().isNotFound());
    }
}
