import java.util.Map;
import java.util.HashMap;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import java.util.List;

public class App {

  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("restaurants", Restaurant.all());
      model.put("cuisines", Cuisine.all());
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("cuisines", Cuisine.all());

      int selectedCuisineType = Integer.parseInt(request.queryParams("cuisine"));
      List<Restaurant> restaurantsByCuisine = Restaurant.findByCuisine(selectedCuisineType);
      String cuisineName = Cuisine.find(selectedCuisineType).getType();

      model.put ("listCuisineName", cuisineName);
      model.put ("restaurants", restaurantsByCuisine);
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/new-restaurant", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("restaurants", Restaurant.all());
      model.put("cuisines", Cuisine.all());
      model.put("template", "templates/new-restaurant.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/new-restaurant", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();

      String newName = request.queryParams("restaurant");
      int cuisine = Integer.parseInt(request.queryParams("cuisine"));
      Restaurant newRestaurant = new Restaurant(newName);
      newRestaurant.save();
      newRestaurant.setCuisineId(cuisine);
      newRestaurant.updateCuisineTypeForNewRestaurant();

      model.put("restaurants", Restaurant.all());
      model.put("cuisines", Cuisine.all());
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/restaurant/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/restaurant.vtl");

      Restaurant restaurant = Restaurant.find(Integer.parseInt(request.params(":id")));
      String cuisineName = Cuisine.find(restaurant.getCuisineId()).getType();

      model.put("restaurants", Restaurant.all());
      model.put("cuisines", Cuisine.all());
      model.put("restaurant", restaurant);
      model.put("cuisineName", cuisineName);

      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/restaurant/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/restaurant.vtl");

      Restaurant restaurant = Restaurant.find(Integer.parseInt(request.params(":id")));
      restaurant.setName(request.queryParams("restaurant"));
      restaurant.setCuisineId(Integer.parseInt(request.queryParams("cuisine")));
      restaurant.update();

      Restaurant updatedRestaurant = Restaurant.find(Integer.parseInt(request.params(":id")));
      String cuisineName = Cuisine.find(updatedRestaurant.getCuisineId()).getType();
      model.put("restaurants", Restaurant.all());
      model.put("cuisines", Cuisine.all());
      model.put("restaurant", updatedRestaurant);
      model.put("cuisineName", cuisineName);

      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());
  } //end of main
} // end of app
