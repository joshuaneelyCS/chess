package server.handlers;
import com.google.gson.Gson;
import service.IncorrectPasswordException;
import service.UserNotFoundException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler {

    private static final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Route createUser() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                UserService.RegisterRequest registerRequest = gson.fromJson(req.body(), UserService.RegisterRequest.class);
                UserService.RegisterResult result = userService.register(registerRequest);
                System.out.println(result);

                res.status(200);
                return gson.toJson(result);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(e);
            }
        };
    }

    public Route login() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {

                UserService.LoginRequest loginRequest = gson.fromJson(req.body(), UserService.LoginRequest.class);
                UserService.LoginResult result = userService.login(loginRequest);

                System.out.println("Here is the result: " + result);
                res.status(200);
                return gson.toJson(result);
            } catch (UserNotFoundException | IncorrectPasswordException e) {
                res.status(500);
                return gson.toJson(e.getMessage());
            }
        };
    }
}
