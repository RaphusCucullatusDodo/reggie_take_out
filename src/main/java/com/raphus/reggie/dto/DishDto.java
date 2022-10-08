package com.raphus.reggie.dto;

import com.raphus.reggie.entity.Dish;
import com.raphus.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 接收前端数据(当前接收dish+flavor)
 */
@Data
public class DishDto extends Dish {

    /**
     * 当前菜品的口味
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    /**
     * 分类名
     */
    private String categoryName;

    /**
     *
     */
    private Integer copies;
}
