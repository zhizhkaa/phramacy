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
    private final String login;
    private final String password;

    private JSONObject userJSON;

    /*
    Сначала создаём объект User с логином и паролем, потом проверяем
    его методом check user
     */

    public User(String login, String password) throws JSONException {
        this.login = login;
        this.password = password;
        //userJSON = readJSON().getJSONObject(login);
    }

    public void checkUser() throws JSONException {
        JSONObject user = readJSON().getJSONObject(login);
        System.out.println(login);
        System.out.println(user.getString("password"));
        System.out.println(user.getString("id"));
        System.out.println(user.getString("access"));
    }

    public String getLogin() { return login; }

    public String getPassword() { return readJSON().getJSONObject(login).getString("password"); }
    public int getAccess() throws JSONException {
        System.out.println("access" + readJSON().getJSONObject(login).getInt("access"));
        return readJSON().getJSONObject(login).getInt("access");
    }

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

    public static void updateJSON(JSONObject jsonObject) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(User.class.getResource("/users.json").getPath()));
            out.write(jsonObject.toString());
            out.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Возвращает сгенерированный String пароль
    public static String generatePassword() {
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
            updateJSON(object);
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
