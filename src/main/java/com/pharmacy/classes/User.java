package com.pharmacy.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.Random;

public class User {
    private String login;
    private String password;
    private int id;
    private int access;

    private JSONObject userJSON;

    /*
    Сначала создаём объект User с логином и паролем, потом проверяем
    его методом check user
     */

    public User(String login, String password) throws JSONException {
        this.login = login;
        userJSON = readJSON().getJSONObject(login);

        this.password = userJSON.getString("password");
        this.id = userJSON.getInt("id");
        this.access = userJSON.getInt("access");
    }

    public void checkUser() throws JSONException {
        JSONObject user = readJSON().getJSONObject(login);
        System.out.println(login);
        System.out.println(user.getString("password"));
        System.out.println(user.getString("id"));
        System.out.println(user.getString("access"));
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public int getAccess() { return access; }

    // Возвращает String Email если он есть у пользователя
    // Возвращает null если его нет
    public String getEmail() throws JSONException {
        JSONObject user = readJSON().getJSONObject(login);
        try { return user.getString("email"); }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // Возвращает JSONObject файла users.json
    public static JSONObject readJSON() throws JSONException {
        InputStream is = User.class.getResourceAsStream("/users.json");
        String jsonTxt = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        JSONObject jsonObject = new JSONObject(jsonTxt);

        return jsonObject;
    }

    // Возвращает сгенерированный String пароль
    static String generatePassword() {
        // Генерируем новый пароль
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8; // 8 знаков
        Random random = new Random();
        String generatedPassword = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedPassword;
    }

    static public String resetPassword(String login) throws JSONException {
        String generatedPassword = generatePassword();

        JSONObject object = readJSON();
        JSONObject user = object.getJSONObject(login);

        if (user != null) {
            user.put("password", generatedPassword);
            object.put(login, user);
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(User.class.getResource("/users.json").getPath()));
                out.write(object.toString());
                out.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return generatedPassword;
        }
        return null;
    }

    public void setPassword(String newPassword) throws JSONException {
        JSONObject object = readJSON();
        JSONObject user = object.getJSONObject(login);

        if (user != null) {
            user.put("password", newPassword);
            object.put(login, user);
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(User.class.getResource("/users.json").getPath()));
                out.write(object.toString());
                out.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
