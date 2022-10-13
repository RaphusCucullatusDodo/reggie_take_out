package com.raphus.reggie.dto;

import com.raphus.reggie.entity.Setmeal;
import com.raphus.reggie.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("套餐DTO")
public class SetmealDto extends Setmeal {

    @ApiModelProperty(value = "套餐所含菜品")
    private List<SetmealDish> setmealDishes;

    @ApiModelProperty("套餐分类名")
    private String categoryName;
}
