package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.to.MealToIn;
import ru.javawebinar.topjava.to.MealToOut;
import ru.javawebinar.topjava.to.MealsFilterTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.validation.Valid;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.parseValidationErrors;

@RestController
@RequestMapping("/ajax/profile/meals")
public class MealUIController extends AbstractMealController {

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealTo> getAll() {
        return super.getAll();
    }

    //@Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MealToOut getToOut(@PathVariable("id") int id) {
        return MealsUtil.asToOut(super.get(id));
    }

    @Override
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdate(@Valid MealToIn mealToIn, BindingResult result) {
        if (result.hasErrors()) {
            return parseValidationErrors(result);
        }
        if (mealToIn.isNew()) {
            super.create(MealsUtil.createNewFromTo(mealToIn));
        } else {
            super.update(mealToIn, mealToIn.id());
        }
        return ResponseEntity.ok().build();
    }

    //@Override
    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealTo> getBetween(MealsFilterTo mealsFilterTo) {
        return super.getBetween(mealsFilterTo.getStartDate(),
                mealsFilterTo.getStartTime(),
                mealsFilterTo.getEndDate(),
                mealsFilterTo.getEndTime());
    }
}