package com.raphus.reggie.dto;

import com.raphus.reggie.entity.Setmeal;
import com.raphus.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
