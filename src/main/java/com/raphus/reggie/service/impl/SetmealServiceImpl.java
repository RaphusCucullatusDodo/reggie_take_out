package com.raphus.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.raphus.reggie.common.CustomException;
import com.raphus.reggie.dto.SetmealDto;
import com.raphus.reggie.entity.Dish;
import com.raphus.reggie.entity.Setmeal;
import com.raphus.reggie.entity.SetmealDish;
import com.raphus.reggie.mapper.SetmealMapper;
import com.raphus.reggie.service.DishService;
import com.raphus.reggie.service.SetmealDishService;
import com.raphus.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐及中间表
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存Setmeal
        this.save(setmealDto);
        //保存SetmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().forEach(item->{
            item.setSetmealId(setmealDto.getId());
//            Dish dish = dishService.getById(item.getDishId());
//            item.setName(dish.getName());
//            item.setPrice(dish.getPrice());
//            item.setSetmealId(setmealDto.getId());
        });
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐及中间表
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //确定是否可以删除(售卖中不可删除) count(*) from Setmeal where status = 1 and id in (id1,id2,id3)
        int count = this.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getStatus,1).in(Setmeal::getId,ids));
        //不能则抛业务异常
        if (count>0){
            throw new CustomException("售卖中不可删除");
        }
        this.removeByIds(ids);
        //获取需要删除的setmealDish delete from SetmealDish where setmealId in (id1,id2,id3)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
//        //获取套餐关联的setmealDish数据
//        List<SetmealDish> list = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().in(SetmealDish::getSetmealId, ids));
//        //获取需要删除的setmealDish的id
//        List<Long> setmealDishIds = list.stream().map(item -> item.getId()).collect(Collectors.toList());
//        setmealDishService.removeByIds(setmealDishIds);

    }
}
