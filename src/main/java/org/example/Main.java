package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Main {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static String createNewUser(String userData) throws IOException {
        return sendRequest(BASE_URL + "/users", "POST", userData);
    }

    public static String updateUser(int userId, String updatedUserData) throws IOException {
        return sendRequest(BASE_URL + "/users/" + userId, "PUT", updatedUserData);
    }

    public static int deleteUser(int userId) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/users/" + userId).openConnection();
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode();
    }

    public static String getAllUsers() throws IOException {
        return sendRequest(BASE_URL + "/users", "GET", null);
    }

    public static String getUserById(int userId) throws IOException {
        return sendRequest(BASE_URL + "/users/" + userId, "GET", null);
    }

    public static String getUserByUsername(String username) throws IOException {
        return sendRequest(BASE_URL + "/users?username=" + username, "GET", null);
    }

    public static void getCommentsForLastPostOfUserAndSaveToFile(int userId, String fileName) throws IOException {
        String lastPostUrl = BASE_URL + "/users/" + userId + "/posts";
        String lastPost = sendRequest(lastPostUrl, "GET", null);
        int lastPostId = getLastPostId(lastPost);
        String commentsUrl = BASE_URL + "/posts/" + lastPostId + "/comments";
        String comments = sendRequest(commentsUrl, "GET", null);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(comments);
        }
    }

    private static int getLastPostId(String posts) {
        JSONArray jsonArray = new JSONArray(posts);
        JSONObject lastPost = jsonArray.getJSONObject(jsonArray.length() - 1);
        return lastPost.getInt("id");
    }

    public static String getOpenTasksForUser(int userId) throws IOException {
        return sendRequest(BASE_URL + "/users/" + userId + "/todos", "GET", null);
    }

    private static String sendRequest(String urlString, String method, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        if (body != null && !body.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    public static void main(String[] args) {
        try {
            String newUser = "{\"name\": \"Oleksandr Davydenko\", \"username\": \"oleksandrdavydenko\", \"email\": \"o.davydenko@gmail.com\"}";
            String createdUser = createNewUser(newUser);
            System.out.println("Created user: " + createdUser);

            int userIdToUpdate = 5;
            String updatedUserData = "{\"name\": \"Leo Messi\", \"username\": \"leomessi\", \"email\": \"leomessi@gmail.com\"}";
            String updatedUser = updateUser(userIdToUpdate, updatedUserData);
            System.out.println("Updated user: " + updatedUser);

            int userIdToDelete = 7;
            int deleteStatus = deleteUser(userIdToDelete);
            System.out.println("Delete status: " + deleteStatus);

            String allUsers = getAllUsers();
            System.out.println("All users: " + allUsers);

            int userIdToGet = 2;
            String userById = getUserById(userIdToGet);
            System.out.println("User by ID: " + userById);

            String usernameToGet = "Bret";
            String userByUsername = getUserByUsername(usernameToGet);
            System.out.println("User by username: " + userByUsername);

            int userIdForComments = 1;
            String fileNameForComments = "user-" + userIdForComments + "-post-last-comments.json";
            getCommentsForLastPostOfUserAndSaveToFile(userIdForComments, fileNameForComments);
            System.out.println("Comments saved to file: " + fileNameForComments);

            int userIdForTasks = 1;
            String openTasksForUser = getOpenTasksForUser(userIdForTasks);
            System.out.println("Open tasks for user: " + openTasksForUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}