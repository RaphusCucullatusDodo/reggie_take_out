package com.raphus.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raphus.reggie.dto.SetmealDto;
import com.raphus.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
