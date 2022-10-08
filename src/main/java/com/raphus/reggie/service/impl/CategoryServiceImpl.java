package com.raphus.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.raphus.reggie.common.CustomException;
import com.raphus.reggie.entity.Category;
import com.raphus.reggie.entity.Dish;
import com.raphus.reggie.entity.Setmeal;
import com.raphus.reggie.mapper.CateGoryMapper;
import com.raphus.reggie.service.CategoryService;
import com.raphus.reggie.service.DishService;
import com.raphus.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CateGoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 通过ID删除分类(未关联菜品)
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count>=1){
            //抛出业务异常
            throw new CustomException("当前分类已关联菜品,不允许删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2>=1){
            //抛出业务异常
            throw new CustomException("当前分类已关联套餐,不允许删除");
        }
        super.removeById(ids);
        return;
    }
}
