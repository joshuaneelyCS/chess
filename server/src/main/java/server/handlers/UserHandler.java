package server.handlers;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.IncorrectPasswordException;
import service.UserNotFoundException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class UserHandler {

    private static final Gson GSON = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Route createUser() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                UserService.RegisterRequest registerRequest = GSON.fromJson(req.body(), UserService.RegisterRequest.class);
                UserService.RegisterResult result = userService.register(registerRequest);
                res.status(200);
                return GSON.toJson(result);

            } catch (DataAccessException e) {
                res.status(403);
                return GSON.toJson(Map.of("message", "Error: Username already exists"));
            } catch (IncorrectPasswordException e) {
                res.status(400);
                return GSON.toJson(Map.of("message", "Error: Please fill in all fields"));
            }
        };
    }

    public Route login() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {

                UserService.LoginRequest loginRequest = GSON.fromJson(req.body(), UserService.LoginRequest.class);
                UserService.LoginResult result = userService.login(loginRequest);

                System.out.println("Here is the result: " + result);
                res.status(200);
                return GSON.toJson(result);
            } catch (UserNotFoundException e) {
                res.status(401);
                return GSON.toJson(Map.of("message", "Error: User not found"));
            } catch (IncorrectPasswordException e) {
                res.status(401);
                return GSON.toJson(Map.of("message", "Error: Incorrect Password"));
            }
        };
    }

    public Route logout() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                String token = req.headers("Authorization");

                if (token == null || token.isEmpty()) {
                    res.status(401);
                    return GSON.toJson("Unauthorized");
                }

                userService.logout(token);

                res.status(200);
                return GSON.toJson(Map.of("message", "Success"));

            } catch (DataAccessException e) {

                res.status(401);
                return GSON.toJson(Map.of("message", "Error: token not found"));
            }
        };
    }


}
