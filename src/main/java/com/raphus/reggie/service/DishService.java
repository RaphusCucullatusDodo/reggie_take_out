package com.raphus.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raphus.reggie.dto.DishDto;
import com.raphus.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
